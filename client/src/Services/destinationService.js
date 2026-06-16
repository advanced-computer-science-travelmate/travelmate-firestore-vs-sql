export async function getEuropeanDestinations() {
  const response = await fetch(
    "http://localhost:8080/api/destinations/europe"
  );

  if (!response.ok) {
    throw new Error("Failed to fetch destinations");
  }

  return await response.json();
}