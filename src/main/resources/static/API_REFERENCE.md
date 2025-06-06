# Globetrotter Services API Reference

Welcome to the Globetrotter Services API! You can use our API to access all endpoints, which can help you integrate travel information and AI capabilities into your applications.

We have language bindings in Shell and JavaScript! You can view code examples in the dark area to the right, and you can switch the programming language of the examples with the tabs in the top right.

## Authentication

Globetrotter Services uses API keys to authenticate requests. You can view and manage your API keys in the Globetrotter Services Dashboard.

Authentication to the API is performed via Bearer Authentication. Provide your API key as the bearer token value.

All API requests must be made over HTTPS. Calls made over plain HTTP will fail. API requests without authentication will also fail.

```shell
# With shell, you can just pass the correct header with each request
curl "https://api.globetrotter-services.com/openai/completion" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Your prompt here"}'
```

```javascript
// Using fetch with JavaScript
fetch('https://api.globetrotter-services.com/openai/completion', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer YOUR_API_KEY',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    prompt: 'Your prompt here'
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

> Make sure to replace `YOUR_API_KEY` with your actual API key.

## Health Check

### Endpoint Health

```http
GET /common/health
```

This endpoint provides basic health status of the API service.

#### Response

```json
"UP"
```

The API returns a simple "UP" response to indicate the service is running properly.

## OpenAI Integration

### Generate Completion

```http
POST /openai/completion
```

This endpoint sends your prompt to OpenAI and returns the completion response.

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| prompt | string | Yes | The text prompt to send to OpenAI |
| maxTokens | integer | No | Maximum number of tokens to generate (default: 100) |

#### Example Request

```json
{
  "prompt": "Write a poem about travel",
  "maxTokens": 150
}
```

#### Response

A successful request returns the raw JSON response from OpenAI's Completion API.

```json
{
  "id": "cmpl-uqkvlQyYK7bGYrRHQ0eXlWi7",
  "object": "text_completion",
  "created": 1689818628,
  "model": "gpt-3.5-turbo-instruct",
  "choices": [
    {
      "text": "Wanderlust pulls at my heart,\nA desire to journey, to depart.\nNew horizons call my name,\nAdventures that set my soul aflame...",
      "index": 0,
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 5,
    "completion_tokens": 42,
    "total_tokens": 47
  }
}
```

#### Error Responses

The API may return various error responses depending on the issue:

**Missing API Key**
```json
{
  "error": "OpenAI API key is missing."
}
```

**Quota Exceeded**
```json
{
  "error": {
    "message": "You exceeded your current quota, please check your plan and billing details.",
    "type": "insufficient_quota",
    "param": null,
    "code": "insufficient_quota"
  }
}
```

## Travel Services

### Search Flights

```http
GET /travel/flights
```

This endpoint retrieves flight information based on origin, destination, and date.

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| from | string | Yes | Origin airport code (e.g., "SFO") |
| to | string | Yes | Destination airport code (e.g., "JFK") |
| date | string | Yes | Flight date in ISO 8601 format (YYYY-MM-DD) |

#### Example Request

```
GET /travel/flights?from=SFO&to=JFK&date=2025-07-15
```

#### Response

```json
{
  "data": [
    {
      "type": "flight-offer",
      "id": "1",
      "source": "GDS",
      "instantTicketingRequired": false,
      "nonHomogeneous": false,
      "oneWay": false,
      "lastTicketingDate": "2025-06-15",
      "itineraries": [
        {
          "duration": "PT6H10M",
          "segments": [
            {
              "departure": {
                "iataCode": "SFO",
                "terminal": "2",
                "at": "2025-07-15T08:00:00"
              },
              "arrival": {
                "iataCode": "JFK",
                "terminal": "4",
                "at": "2025-07-15T16:10:00"
              },
              "carrierCode": "DL",
              "number": "586",
              "aircraft": {
                "code": "76W"
              },
              "operating": {
                "carrierCode": "DL"
              },
              "duration": "PT6H10M",
              "id": "1",
              "numberOfStops": 0
            }
          ]
        }
      ],
      "price": {
        "currency": "USD",
        "total": "325.94",
        "base": "250.00",
        "fees": [
          {
            "amount": "0.00",
            "type": "SUPPLIER"
          },
          {
            "amount": "0.00",
            "type": "TICKETING"
          }
        ],
        "grandTotal": "325.94"
      }
    }
  ]
}
```

## Natural Language Flight Search

### Extract Flight Search Parameters

This endpoint is not directly exposed but is used internally by the OpenAI service. It extracts flight search parameters from natural language text.

Example input:
```
"I need a flight from San Francisco to New York on July 15, 2025 for 2 people in business class"
```

Processed output (converted to a FlightSearchRequest object):
```json
{
  "from": "SFO",
  "to": "JFK",
  "fromDate": "2025-07-15",
  "toDate": null,
  "numberOfPassengers": 2,
  "travelClass": "BUSINESS"
}
```

## Errors

The Globetrotter Services API uses conventional HTTP response codes to indicate the success or failure of an API request.

| Code | Description |
|------|-------------|
| 200 - OK | Everything worked as expected |
| 400 - Bad Request | The request was unacceptable, often due to missing a required parameter |
| 401 - Unauthorized | No valid API key provided |
| 403 - Forbidden | The API key doesn't have permissions to perform the request |
| 404 - Not Found | The requested resource doesn't exist |
| 429 - Too Many Requests | Too many requests hit the API too quickly |
| 500, 502, 503, 504 - Server Errors | Something went wrong on the server |

## Rate Limits

The API has rate limits to prevent abuse. Rate limits are applied on a per-API key basis.

If you exceed the rate limits, you'll receive a 429 Too Many Requests response.
