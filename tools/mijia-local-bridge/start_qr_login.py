#!/usr/bin/env python3.10
import json
import sys
import time
import uuid
from pathlib import Path
from urllib import parse

import requests


def print_json(payload):
    print(json.dumps(payload, ensure_ascii=False))


def main():
    try:
        from mijiaAPI.apis import mijiaAPI
    except ModuleNotFoundError:
        print_json({
            "status": "error",
            "error": "PACKAGE_MISSING",
            "message": "未安装 mijiaAPI，请先安装后再生成二维码",
            "installCommand": "python3.10 -m pip install mijiaAPI==3.0.5"
        })
        return

    sessions_dir = Path(__file__).resolve().parent / ".qr-sessions"
    sessions_dir.mkdir(parents=True, exist_ok=True)
    api = mijiaAPI()

    try:
        location_data = api._get_location()
        if location_data.get("code") == 0 and location_data.get("message") == "刷新Token成功":
            print_json({
                "status": "authenticated",
                "message": "当前本机已存在有效米家登录态，无需再次扫码",
                "authPath": str(api.auth_data_path)
            })
            return

        location_data.update({
            "theme": "",
            "bizDeviceType": "",
            "_hasLogo": "false",
            "_qrsize": "240",
            "_dc": str(int(time.time() * 1000))
        })
        url = api.login_url + "?" + parse.urlencode(location_data)
        headers = {
            "User-Agent": api.user_agent,
            "Accept-Encoding": "gzip",
            "Content-Type": "application/x-www-form-urlencoded",
            "Connection": "keep-alive"
        }
        login_ret = requests.get(url, headers=headers, timeout=20)
        login_data = api._handle_ret(login_ret)

        session_id = uuid.uuid4().hex
        state_path = sessions_dir / f"{session_id}.json"
        state_payload = {
            "sessionId": session_id,
            "createdAt": int(time.time() * 1000),
            "authData": api.auth_data,
            "loginData": {
                "lp": login_data["lp"],
                "qr": login_data["qr"],
                "loginUrl": login_data["loginUrl"]
            }
        }
        state_path.write_text(json.dumps(state_payload, ensure_ascii=False, indent=2), encoding="utf-8")
        print_json({
            "status": "pending",
            "sessionId": session_id,
            "qrImageUrl": login_data["qr"],
            "loginUrl": login_data["loginUrl"],
            "expiresInSeconds": 180,
            "message": "二维码已生成，请使用米家 App 扫码登录"
        })
    except Exception as exc:
        print_json({
            "status": "error",
            "error": "START_FAILED",
            "message": str(exc)
        })


if __name__ == "__main__":
    main()
