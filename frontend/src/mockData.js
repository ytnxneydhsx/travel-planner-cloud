export const fallbackDestinations = [
  {
    id: 101,
    name: "West Lake Morning Walk",
    regionName: "Hangzhou",
    regionCode: "330100",
    summary: "Misty causeways, tea-scented lanes, and a soft sunrise loop.",
    coverImageUrl: "https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: 102,
    name: "Suzhou Garden Pause",
    regionName: "Suzhou",
    regionCode: "320500",
    summary: "A quiet garden afternoon stitched together with canals and stone bridges.",
    coverImageUrl: "https://images.unsplash.com/photo-1590390426090-7e88f330b173?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: 103,
    name: "Qingdao Sea Wind Route",
    regionName: "Qingdao",
    regionCode: "370200",
    summary: "Coastal roads, old town roofs, and a golden-hour beer street finish.",
    coverImageUrl: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80"
  }
];

export const fallbackItinerary = {
  id: "draft",
  title: "Soft city weekend",
  description: "A low-pressure route with scenic walks, one garden pause, and evening food streets.",
  destinations: [
    {
      destinationId: 101,
      name: "West Lake Morning Walk",
      regionName: "Hangzhou",
      summary: "Misty causeways and a soft sunrise loop.",
      sortOrder: 1
    },
    {
      destinationId: 102,
      name: "Suzhou Garden Pause",
      regionName: "Suzhou",
      summary: "Canals, stone bridges, and a quiet garden afternoon.",
      sortOrder: 2
    }
  ]
};
