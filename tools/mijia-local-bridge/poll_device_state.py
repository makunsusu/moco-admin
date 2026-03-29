#!/usr/bin/env python3.10
import json
import sys
from pathlib import Path
from typing import Dict, List, Optional, Tuple

import requests


SPEC_CACHE: Dict[str, dict] = {}

POWER_TYPE_KEYWORDS = [
    ":property:on:",
    ":property:power:",
    "switch-status",
    "switch_status",
]


def sanitize_payload(payload):
    if isinstance(payload, dict):
        sanitized = {}
        for key, value in payload.items():
            if key in {"token"}:
                continue
            sanitized[key] = sanitize_payload(value)
        return sanitized
    if isinstance(payload, list):
        return [sanitize_payload(item) for item in payload]
    return payload


def emit(payload, code=0):
    print(json.dumps(payload, ensure_ascii=False))
    sys.exit(code)


def normalize_power_value(value):
    if isinstance(value, bool):
        return "ON" if value else "OFF"
    text = str(value).strip().lower()
    if text in {"1", "true", "on"}:
        return "ON"
    if text in {"0", "false", "off"}:
        return "OFF"
    return "UNKNOWN"


def load_spec(spec_type: str) -> Optional[dict]:
    if not spec_type:
        return None
    if spec_type in SPEC_CACHE:
        return SPEC_CACHE[spec_type]
    try:
        response = requests.get(
            "https://miot-spec.org/miot-spec-v2/instance",
            params={"type": spec_type},
            timeout=15
        )
        response.raise_for_status()
        payload = response.json()
        SPEC_CACHE[spec_type] = payload
        return payload
    except Exception:
        SPEC_CACHE[spec_type] = None
        return None


def find_power_prop(spec: Optional[dict]) -> Optional[Tuple[int, int, str]]:
    if not spec:
        return None
    best = None
    best_score = -1
    for service in spec.get("services") or []:
        siid = service.get("iid")
        for prop in service.get("properties") or []:
            access = prop.get("access") or []
            if "read" not in access:
                continue
            ptype = str(prop.get("type") or "").lower()
            desc = str(prop.get("description") or "").lower()
            score = -1
            if any(keyword in ptype for keyword in POWER_TYPE_KEYWORDS):
                score = 100
            elif desc in {"on", "power", "switch status"}:
                score = 80
            elif prop.get("format") == "bool" and any(word in desc for word in ["power", "switch", "on"]):
                score = 60
            if score > best_score:
                best_score = score
                best = (siid, prop.get("iid"), prop.get("description") or ptype)
    return best


def main():
    auth_path = Path.home() / ".config" / "mijia-api" / "auth.json"
    python_info = sys.executable
    try:
        from mijiaAPI import mijiaAPI
    except Exception as exc:
        emit({
            "status": "error",
            "error": "PACKAGE_MISSING",
            "message": f"未安装 mijiaAPI：{exc}",
            "installCommand": "python3.10 -m pip install mijiaAPI==3.0.5",
            "python": python_info
        })

    try:
        api = mijiaAPI()
    except Exception as exc:
        emit({
            "status": "error",
            "error": "API_INIT_FAILED",
            "message": str(exc),
            "authPath": str(auth_path),
            "python": python_info
        })

    if not getattr(api, "available", False):
        emit({
            "status": "error",
            "error": "AUTH_REQUIRED",
            "message": "本机未完成米家扫码登录，或登录态已失效。",
            "loginCommand": "mijiaAPI -l",
            "authPath": str(auth_path),
            "python": python_info
        })

    try:
        devices = list(api.get_devices_list()) + list(api.get_shared_devices_list())
    except Exception as exc:
        emit({
            "status": "error",
            "error": "LIST_FAILED",
            "message": str(exc),
            "python": python_info
        })

    power_queries: List[dict] = []
    power_query_meta: Dict[str, str] = {}
    normalized = []
    for item in devices:
        spec_type = item.get("spec_type") or ""
        power_prop = find_power_prop(load_spec(spec_type))
        did = str(item.get("did") or "")
        if power_prop and item.get("isOnline"):
            siid, piid, label = power_prop
            power_queries.append({
                "did": did,
                "siid": siid,
                "piid": piid
            })
            power_query_meta[did] = f"{siid}.{piid}:{label}"
        normalized.append({
            "did": did,
            "uid": str(item.get("uid") or ""),
            "homeId": str(item.get("home_id") or item.get("homeId") or item.get("ssid") or ""),
            "homeName": item.get("home_name") or item.get("homeName") or item.get("ssid") or "",
            "roomId": str(item.get("room_id") or item.get("roomId") or ""),
            "roomName": item.get("room_name") or item.get("roomName") or item.get("roomNameStr") or "未分配房间",
            "deviceName": item.get("name") or item.get("device_name") or item.get("deviceName") or did,
            "model": item.get("model") or "",
            "onlineStatus": "1" if item.get("isOnline") or item.get("online") else "0",
            "powerStatus": "UNKNOWN",
            "properties": {},
            "rawPayload": sanitize_payload(item),
        })

    power_result_map: Dict[str, str] = {}
    if power_queries:
        try:
            prop_results = api.get_devices_prop(power_queries)
            for result in prop_results or []:
                if result.get("code") == 0:
                    power_result_map[str(result.get("did") or "")] = normalize_power_value(result.get("value"))
        except Exception:
            power_result_map = {}

    for item in normalized:
        did = item["did"]
        if did in power_result_map:
            item["powerStatus"] = power_result_map[did]
        if did in power_query_meta:
            item["properties"]["powerProbe"] = power_query_meta[did]

    emit({
        "status": "success",
        "deviceCount": len(normalized),
        "devices": normalized,
        "authPath": str(auth_path),
        "python": python_info
    })


if __name__ == "__main__":
    main()
