#!/bin/bash
# Globetrotter Services API Examples using curl

# Set your API key here
API_KEY="YOUR_API_KEY"

# Base URL for the API
API_URL="http://localhost:8080"

# Text styling
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check health endpoint
echo -e "${BLUE}Checking API health...${NC}"
HEALTH_RESPONSE=$(curl -s "${API_URL}/common/health" \
  -H "Authorization: Bearer ${API_KEY}")

echo -e "${GREEN}Health status:${NC} ${HEALTH_RESPONSE}"
echo

# Generate text using OpenAI
echo -e "${BLUE}Generating text with OpenAI...${NC}"
COMPLETION_RESPONSE=$(curl -s "${API_URL}/openai/completion" \
  -H "Authorization: Bearer ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Write a short poem about travel",
    "maxTokens": 150
  }')

# Check if the response contains an error
if [[ $COMPLETION_RESPONSE == *"error"* ]]; then
  echo -e "${RED}Error generating text:${NC}"
  echo $COMPLETION_RESPONSE | jq
else
  echo -e "${GREEN}Generated text:${NC}"
  echo $COMPLETION_RESPONSE | jq '.choices[0].text'
fi
echo

# Search for flights
echo -e "${BLUE}Searching for flights...${NC}"
FLIGHT_RESPONSE=$(curl -s "${API_URL}/travel/flights?from=SFO&to=JFK&date=2025-07-15" \
  -H "Authorization: Bearer ${API_KEY}")

if [[ $FLIGHT_RESPONSE == *"error"* ]]; then
  echo -e "${RED}Error searching flights:${NC}"
  echo $FLIGHT_RESPONSE | jq
else
  echo -e "${GREEN}Flight results:${NC}"
  echo $FLIGHT_RESPONSE | jq
fi

echo -e "${BLUE}Examples completed.${NC}"
