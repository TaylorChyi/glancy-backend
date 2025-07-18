#!/bin/bash
# Quick API smoke test using curl
# BASE_URL defaults to http://localhost:8080
# Optional third-party URLs can be set with environment variables like
# THIRDPARTY_DEEPSEEK_BASE_URL or THIRDPARTY_OPENAI_BASE_URL.

BASE_URL=${BASE_URL:-http://localhost:8080}
ADMIN_AUTH="-u admin:password"

function section() {
    echo -e "\n== $1 =="
}

section "Ping"
curl -i "$BASE_URL/api/ping"

section "Register user"
curl -i -H "Content-Type: application/json" \
    -d '{"username":"demo","password":"pass123","email":"demo@example.com","phone":"555"}' \
    "$BASE_URL/api/users/register"

section "Login"
curl -i -H "Content-Type: application/json" \
    -d '{"username":"demo","password":"pass123"}' \
    "$BASE_URL/api/users/login"

section "Login by phone"
curl -i -H "Content-Type: application/json" \
    -d '{"phone":"555","password":"pass123"}' \
    "$BASE_URL/api/users/login"

section "Create FAQ"
curl -i -H "Content-Type: application/json" \
    -d '{"question":"What?","answer":"It works"}' \
    "$BASE_URL/api/faqs"

section "List FAQs"
curl -i "$BASE_URL/api/faqs"

section "Create system notification"
curl -i $ADMIN_AUTH -H "Content-Type: application/json" \
    -d '{"message":"System notice"}' \
    "$BASE_URL/api/notifications/system"

section "Create user notification"
curl -i -H "Content-Type: application/json" \
    -d '{"message":"Hi"}' \
    "$BASE_URL/api/notifications/user/1"

section "List user notifications"
curl -i "$BASE_URL/api/notifications/user/1"

section "Submit contact message"
curl -i -H "Content-Type: application/json" \
    -d '{"name":"Tester","email":"test@example.com","message":"Hello"}' \
    "$BASE_URL/api/contact"

section "Save user preference"
curl -i -H "Content-Type: application/json" \
    -d '{"theme":"light","systemLanguage":"en","searchLanguage":"en","dictionaryModel":"QIANWEN"}' \
    "$BASE_URL/api/preferences/user/1"

section "Get user preference"
curl -i "$BASE_URL/api/preferences/user/1"

section "Add search record"
curl -i -H "Content-Type: application/json" \
    -d '{"term":"hello","language":"ENGLISH"}' \
    "$BASE_URL/api/search-records/user/1"

section "List search records"
curl -i "$BASE_URL/api/search-records/user/1"

section "Lookup word"
curl -i "$BASE_URL/api/words?userId=1&term=hello&language=ENGLISH"

section "Clear search records"
curl -i -X DELETE "$BASE_URL/api/search-records/user/1"

section "Portal user stats"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/user-stats"

section "Daily active users"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/daily-active"

section "Add system parameter"
curl -i $ADMIN_AUTH -H "Content-Type: application/json" \
    -d '{"name":"motd","value":"hello"}' \
    "$BASE_URL/api/portal/parameters"

section "Get system parameter"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/parameters/motd"

section "List system parameters"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/parameters"

section "Add alert recipient"
curl -i $ADMIN_AUTH -H "Content-Type: application/json" \
    -d '{"email":"alert@example.com"}' \
    "$BASE_URL/api/portal/alert-recipients"

section "List alert recipients"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/alert-recipients"

section "Update alert recipient"
curl -i -X PUT $ADMIN_AUTH -H "Content-Type: application/json" \
    -d '{"email":"alert2@example.com"}' \
    "$BASE_URL/api/portal/alert-recipients/1"

section "Delete alert recipient"
curl -i -X DELETE $ADMIN_AUTH "$BASE_URL/api/portal/alert-recipients/1"

section "Set email notifications enabled"
curl -i -X POST $ADMIN_AUTH "$BASE_URL/api/portal/email-enabled?enabled=true"

section "Get email notifications enabled"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/email-enabled"

section "Record portal traffic"
curl -i $ADMIN_AUTH -H "Content-Type: application/json" \
    -d '{"path":"/","ip":"127.0.0.1","userAgent":"curl"}' \
    "$BASE_URL/api/portal/traffic"

section "Daily traffic counts"
curl -i $ADMIN_AUTH "$BASE_URL/api/portal/traffic/daily?start=2024-01-01&end=2024-01-02"

