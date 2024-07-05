# cors-demo

Commands to run in the terminal to check CORS:</br>

```angular2html
curl -I -X OPTIONS http://localhost:9080/users -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: GET" -H "Access-Control-Request-Headers: Content-Type"
```