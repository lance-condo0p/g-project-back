## Authentification

All requests should use basic authentication. 
The only exception is "GET /api/openapi" endpoint.

For testing purposes: username = `foo` and password = `bar`.

## Request Headers

Use: `content-type: application/json; charset=UTF-8`

## OpenAPI

Open in a web browser:

"http://host:12222/api/openapi"

## Examples: Transcription

### Request

"POST http://host:12222/api/v1/transcribe"

`{
"format": "OGG",
"file_base64": "..."
}`

### Response

**SC 200**

`{
"is_successful": true,
"transcription": "1 2 3 4 5 6"
}`

**SC 400**

`{
"is_successful": false
}`