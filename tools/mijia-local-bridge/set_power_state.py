#!/usr/bin/env python3.10
import json
import sys
from pathlib import Path
from typing import Dict, Optional, Tuple

import requests


SPEC_CACHE: Dict[str, dict] = {}

POWER_TYPE_KEYWORDS = [
    ":property:on:",
    ":property:power:",
    "switch-status",
    "switch_status",
]


def emit(payload, code=0):
    print(json.dumps(payload, ensure_ascii=False))
    sys.exit(code)


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
            if "read" not in access or "write" not in access:
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
    if len(sys.argv) < 3:
        emit({
            "status": "error",
            "error": "INVALID_ARGS",
            "message": "缺少设备 DID 或控制动作"
        }, 1)
    did = sys.argv[1].strip()
    target = sys.argv[2].strip().lower()
    if target not in {"on", "off"}:
        emit({
            "status": "error",
            "error": "INVALID_ACTION",
            "message": "控制动作仅支持 on / off"
        }, 1)

    auth_path = Path.home() / ".config" / "mijia-api" / "auth.json"
    try:
        from mijiaAPI import mijiaAPI
    except Exception as exc:
        emit({
            "status": "error",
            "error": "PACKAGE_MISSING",
            "message": f"未安装 mijiaAPI：{exc}",
            "installCommand": "python3.10 -m pip install mijiaAPI==3.0.5"
        }, 1)

    api = mijiaAPI()
    if not getattr(api, "available", False):
        emit({
            "status": "error",
            "error": "AUTH_REQUIRED",
            "message": "本机未完成米家扫码登录，或登录态已失效。",
            "loginCommand": "mijiaAPI -l",
            "authPath": str(auth_path)
        }, 1)

    try:
        devices = list(api.get_devices_list()) + list(api.get_shared_devices_list())
    except Exception as exc:
        emit({
            "status": "error",
            "error": "LIST_FAILED",
            "message": str(exc)
        }, 1)

    device = next((item for item in devices if str(item.get("did") or "") == did), None)
    if not device:
        emit({
            "status": "error",
            "error": "DEVICE_NOT_FOUND",
            "message": "未在本机米家登录态中找到该设备"
        }, 1)

    power_prop = find_power_prop(load_spec(device.get("spec_type") or ""))
    if not power_prop:
        emit({
            "status": "error",
            "error": "POWER_PROP_NOT_FOUND",
            "message": "当前设备未识别到可写的标准开关属性，暂不支持页面开关控制"
        }, 1)

    siid, piid, label = power_prop
    try:
        result = api.set_devices_prop({
            "did": did,
            "siid": siid,
            "piid": piid,
            "value": target == "on"
        })
    except Exception as exc:
        emit({
            "status": "error",
            "error": "SET_FAILED",
            "message": str(exc)
        }, 1)

    code = result.get("code", 1)
    if code not in (0, 1):
        emit({
            "status": "error",
            "error": "SET_FAILED",
            "message": result.get("message") or "设备开关控制失败",
            "result": result
        }, 1)

    emit({
        "status": "success",
        "did": did,
        "target": target.upper(),
        "powerProbe": f"{siid}.{piid}:{label}",
        "result": result
    })


if __name__ == "__main__":
    main()
