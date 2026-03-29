#!/usr/bin/env python3.10
import json
import sys
import time
from datetime import datetime, timedelta
from pathlib import Path

import requests


def print_json(payload):
    print(json.dumps(payload, ensure_ascii=False))


def main():
    if len(sys.argv) < 2 or not sys.argv[1].strip():
        print_json({
            "status": "error",
            "error": "MISSING_SESSION_ID",
            "message": "缺少二维码会话标识"
        })
        return

    session_id = sys.argv[1].strip()
    state_path = Path(__file__).resolve().parent / ".qr-sessions" / f"{session_id}.json"
    if not state_path.exists():
        print_json({
            "status": "expired",
            "error": "SESSION_NOT_FOUND",
            "message": "二维码会话不存在或已过期，请重新生成二维码"
        })
        return

    try:
        from mijiaAPI.apis import mijiaAPI
    except ModuleNotFoundError:
        print_json({
            "status": "error",
            "error": "PACKAGE_MISSING",
            "message": "未安装 mijiaAPI，请先安装后再扫码登录",
            "installCommand": "python3.10 -m pip install mijiaAPI==3.0.5"
        })
        return

    try:
        state_payload = json.loads(state_path.read_text(encoding="utf-8"))
        created_at = int(state_payload.get("createdAt") or 0)
        if created_at and int(time.time() * 1000) - created_at > 3 * 60 * 1000:
            state_path.unlink(missing_ok=True)
            print_json({
                "status": "expired",
                "error": "QR_EXPIRED",
                "message": "二维码已过期，请重新生成"
            })
            return

        api = mijiaAPI()
        api.auth_data = state_payload.get("authData") or {}
        login_data = state_payload.get("loginData") or {}
        headers = {
            "User-Agent": api.user_agent,
            "Accept-Encoding": "gzip",
            "Content-Type": "application/x-www-form-urlencoded",
            "Connection": "keep-alive"
        }
        session = requests.Session()
        try:
            lp_ret = session.get(login_data["lp"], headers=headers, timeout=5)
            lp_data = api._handle_ret(lp_ret)
        except requests.exceptions.Timeout:
            print_json({
                "status": "pending",
                "sessionId": session_id,
                "message": "等待扫码确认中"
            })
            return

        auth_keys = ["psecurity", "nonce", "ssecurity", "passToken", "userId", "cUserId"]
        for key in auth_keys:
            api.auth_data[key] = lp_data[key]
        callback_url = lp_data["location"]
        session.get(callback_url, headers=headers, timeout=20)
        cookies = session.cookies.get_dict()
        api.auth_data.update(cookies)
        api.auth_data.update({
            "expireTime": int((datetime.now() + timedelta(days=30)).timestamp() * 1000)
        })
        api._save_auth_data()
        state_path.unlink(missing_ok=True)
        print_json({
            "status": "success",
            "message": "扫码登录成功，已写入本机米家登录态",
            "authPath": str(api.auth_data_path)
        })
    except Exception as exc:
        print_json({
            "status": "error",
            "error": "POLL_FAILED",
            "message": str(exc)
        })


if __name__ == "__main__":
    main()
