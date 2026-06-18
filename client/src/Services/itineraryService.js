export async function searchPlacesByDestination(destination, query) {
  if (!query.trim()) {
    return [];
  }

  const response = await fetch(
    `http://localhost:8080/api/places/search?location=${encodeURIComponent(
      destination
    )}&query=${encodeURIComponent(query)}`
  );

  if (!response.ok) {
    throw new Error("Failed to search places");
  }

  return await response.json();
}