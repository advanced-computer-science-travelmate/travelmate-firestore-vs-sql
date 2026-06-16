export async function getHotelsByDestination(destination) {
  const response = await fetch(
    `http://localhost:8080/api/hotels?location=${encodeURIComponent(
      destination
    )}`
  );

  if (!response.ok) {
    throw new Error("Failed to fetch hotels");
  }

  return await response.json();
}