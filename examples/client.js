// Globetrotter Services API Client Example
// This file demonstrates how to interact with the Globetrotter Services API

class GlobetrotterClient {
  constructor(apiKey, baseUrl = 'https://api.globetrotter-services.com') {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  // Common headers for all requests
  get headers() {
    return {
      'Authorization': `Bearer ${this.apiKey}`,
      'Content-Type': 'application/json'
    };
  }

  // Health check endpoint
  async checkHealth() {
    try {
      const response = await fetch(`${this.baseUrl}/common/health`, {
        method: 'GET',
        headers: this.headers
      });
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.text();
    } catch (error) {
      console.error('Health check failed:', error);
      throw error;
    }
  }

  // Generate text using OpenAI
  async generateCompletion(prompt, maxTokens = 100) {
    try {
      const response = await fetch(`${this.baseUrl}/openai/completion`, {
        method: 'POST',
        headers: this.headers,
        body: JSON.stringify({
          prompt,
          maxTokens
        })
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.error?.message || 
          errorData.error || 
          `API error: ${response.status}`
        );
      }
      
      return await response.json();
    } catch (error) {
      console.error('Text generation failed:', error);
      throw error;
    }
  }

  // Search for flights
  async searchFlights(from, to, date) {
    try {
      // Format date as ISO string YYYY-MM-DD if it's a Date object
      if (date instanceof Date) {
        date = date.toISOString().split('T')[0];
      }
      
      const url = new URL(`${this.baseUrl}/travel/flights`);
      url.searchParams.append('from', from);
      url.searchParams.append('to', to);
      url.searchParams.append('date', date);
      
      const response = await fetch(url, {
        method: 'GET',
        headers: this.headers
      });
      
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('Flight search failed:', error);
      throw error;
    }
  }
}

// Usage example
async function main() {
  // Create client with your API key
  const client = new GlobetrotterClient('YOUR_API_KEY', 'http://localhost:8080');
  
  try {
    // Check API health
    console.log('Checking API health...');
    const health = await client.checkHealth();
    console.log('Health status:', health);
    
    // Generate text using OpenAI
    console.log('\nGenerating text...');
    const completion = await client.generateCompletion('Write a short poem about travel');
    console.log('Generated text:', completion);
    
    // Search for flights
    console.log('\nSearching for flights...');
    const flights = await client.searchFlights('SFO', 'JFK', '2025-07-15');
    console.log('Flight results:', flights);
    
  } catch (error) {
    console.error('Error:', error.message);
  }
}

// Uncomment to run the example
// main();

// Export the client for module usage
if (typeof module !== 'undefined') {
  module.exports = GlobetrotterClient;
}
