export async function getHotelsByDestination(destination) {
  const response = await fetch(
    `http://localhost:8080/api/travel/trips/hotels/search?destination=${encodeURIComponent(
      destination
    )}`
  );

  if (!response.ok) {
    throw new Error("Failed to fetch hotels from Google Places API layer");
  }

  return await response.json();
}