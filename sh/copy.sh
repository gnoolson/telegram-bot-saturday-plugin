#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

PLUGINS_FOLDER=/data/project/java/saturday/plugins
cp -f ../target/telegram-bot-plugin.jar $PLUGINS_FOLDER/tg/telegram-bot-plugin.jar

