import React, { useEffect, useMemo, useState, useTransition } from "react";
import { createRoot } from "react-dom/client";
import {
  addDestinationToItinerary,
  clearSession,
  createItinerary,
  getCurrentUser,
  listItineraries,
  loginUser,
  readSession,
  registerUser,
  removeDestinationFromItinerary,
  saveSession,
  searchDestinations
} from "./api";
import { fallbackDestinations, fallbackItinerary } from "./mockData";
import "./styles.css";

const demoCredentials = {
  username: "traveler_test",
  password: "travel123",
  nickname: "FanOne"
};

function Icon({ name }) {
  const paths = {
    compass: "M12 2l3 7 7 3-7 3-3 7-3-7-7-3 7-3 3-7Zm0 7a3 3 0 1 0 0 6 3 3 0 0 0 0-6Z",
    map: "M9 18 3 21V6l6-3 6 3 6-3v15l-6 3-6-3Zm0 0V3m6 18V6",
    route: "M5 6a3 3 0 1 1 5.2 2.1L8 11l-2.2-2.9A3 3 0 0 1 5 6Zm9 12a3 3 0 1 1 6 0 3 3 0 0 1-6 0ZM8 14h4a4 4 0 0 1 4 4",
    user: "M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm7 8a7 7 0 0 0-14 0",
    plus: "M12 5v14M5 12h14",
    search: "M11 5a6 6 0 1 0 0 12 6 6 0 0 0 0-12Zm4.5 10.5L20 20"
  };

  return (
    <svg className="icon" viewBox="0 0 24 24" aria-hidden="true">
      <path d={paths[name]} />
    </svg>
  );
}

function App() {
  const initialSession = readSession();
  const [user, setUser] = useState(initialSession.user);
  const [authMode, setAuthMode] = useState("login");
  const [authForm, setAuthForm] = useState(demoCredentials);
  const [destinations, setDestinations] = useState(fallbackDestinations);
  const [itineraries, setItineraries] = useState([fallbackItinerary]);
  const [selectedItineraryId, setSelectedItineraryId] = useState("draft");
  const [keyword, setKeyword] = useState("route");
  const [draftTitle, setDraftTitle] = useState("Soft city weekend");
  const [draftDescription, setDraftDescription] = useState(
    "A low-pressure route with scenic walks, one garden pause, and evening food streets."
  );
  const [selectedDestinationIds, setSelectedDestinationIds] = useState([101, 102]);
  const [notice, setNotice] = useState("Using spacious test workspace. Connect the test stack to load live data.");
  const [isPending, startTransition] = useTransition();

  const selectedItinerary = useMemo(
    () => itineraries.find((item) => String(item.id) === String(selectedItineraryId)) || itineraries[0],
    [itineraries, selectedItineraryId]
  );

  const selectedDestinations = useMemo(
    () => destinations.filter((destination) => selectedDestinationIds.includes(destination.id)),
    [destinations, selectedDestinationIds]
  );

  useEffect(() => {
    if (!initialSession.token) {
      return;
    }

    getCurrentUser()
      .then((currentUser) => {
        setUser(currentUser);
        return refreshWorkspace("route");
      })
      .catch(() => {
        clearSession();
        setUser(null);
      });
  }, []);

  async function refreshWorkspace(nextKeyword = keyword) {
    const [destinationData, itineraryData] = await Promise.all([
      searchDestinations(nextKeyword),
      listItineraries()
    ]);

    startTransition(() => {
      setDestinations(destinationData.length > 0 ? destinationData : fallbackDestinations);
      if (itineraryData.length > 0) {
        setItineraries(itineraryData);
        setSelectedItineraryId(itineraryData[0].id);
        setDraftTitle(itineraryData[0].title);
        setDraftDescription(itineraryData[0].description || "");
        setSelectedDestinationIds((itineraryData[0].destinations || []).map((item) => item.destinationId));
      }
      setNotice("Live test environment connected through gateway-service.");
    });
  }

  async function handleAuth(event) {
    event.preventDefault();
    try {
      if (authMode === "register") {
        await registerUser({ ...authForm, status: 1 });
      }
      const loginResponse = await loginUser({
        username: authForm.username,
        password: authForm.password
      });
      saveSession(loginResponse);
      setUser(loginResponse.user);
      await refreshWorkspace();
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleSearch(event) {
    event.preventDefault();
    try {
      const data = await searchDestinations(keyword);
      setDestinations(data.length > 0 ? data : fallbackDestinations);
      setNotice(data.length > 0 ? "Destinations refreshed from test gateway." : "No live matches; showing design seed data.");
    } catch (error) {
      setNotice(`Gateway unavailable: ${error.message}. Showing design seed data.`);
      setDestinations(fallbackDestinations);
    }
  }

  async function handleCreateItinerary() {
    try {
      const response = await createItinerary({
        title: draftTitle,
        description: draftDescription,
        destinationIds: selectedDestinationIds.filter((id) => typeof id === "number" && id > 0)
      });
      setItineraries([response, ...itineraries.filter((item) => item.id !== "draft")]);
      setSelectedItineraryId(response.id);
      setNotice("Itinerary created in the test environment.");
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleAddDestination(destination) {
    if (!selectedDestinationIds.includes(destination.id)) {
      setSelectedDestinationIds([...selectedDestinationIds, destination.id]);
    }

    if (!selectedItinerary || selectedItinerary.id === "draft") {
      return;
    }

    try {
      const updated = await addDestinationToItinerary(selectedItinerary.id, destination.id);
      setItineraries(itineraries.map((item) => (item.id === updated.id ? updated : item)));
      setNotice(`${destination.name} added to the live itinerary.`);
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleRemoveDestination(destinationId) {
    setSelectedDestinationIds(selectedDestinationIds.filter((id) => id !== destinationId));

    if (!selectedItinerary || selectedItinerary.id === "draft") {
      return;
    }

    try {
      const updated = await removeDestinationFromItinerary(selectedItinerary.id, destinationId);
      setItineraries(itineraries.map((item) => (item.id === updated.id ? updated : item)));
      setNotice("Stop removed from the live itinerary.");
    } catch (error) {
      setNotice(error.message);
    }
  }

  function handleLogout() {
    clearSession();
    setUser(null);
    setItineraries([fallbackItinerary]);
    setSelectedItineraryId("draft");
    setNotice("Signed out. The interface is showing design seed data.");
  }

  return (
    <main className="app-shell">
      <aside className="sidebar" aria-label="Primary navigation">
        <div className="brand-mark">
          <span className="brand-symbol">RL</span>
          <span>Route Lantern</span>
        </div>
        <nav className="nav-list">
          <a className="active" href="#destinations"><Icon name="compass" />Destinations</a>
          <a href="#trips"><Icon name="map" />My Trips</a>
          <a href="#planner"><Icon name="route" />Planner</a>
          <a href="#profile"><Icon name="user" />Profile</a>
        </nav>
        <div className="trace-card">
          <span>Trace check</span>
          <strong>X-Trace-Id travels HTTP {"->"} Dubbo</strong>
        </div>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <form className="search-box" onSubmit={handleSearch}>
            <Icon name="search" />
            <input
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder="Search destinations, cities, mood"
              aria-label="Search destinations"
            />
          </form>
          <div className="user-area">
            <span>{user ? `Hello, ${user.nickname || user.username}` : "Test gateway ready"}</span>
            {user ? (
              <button className="ghost-button" onClick={handleLogout}>Sign out</button>
            ) : null}
          </div>
        </header>

        <section className="hero-panel">
          <div>
            <h1>
              <span>Plan a softer</span>
              <span>route through</span>
              <span>the city</span>
            </h1>
            <p>
              Browse live destinations from the test gateway, collect stops, then publish a route that keeps the day light.
            </p>
            <div className="hero-actions">
              <button className="primary-button" onClick={handleCreateItinerary} disabled={!user || isPending}>
                Create itinerary
              </button>
              <a className="secondary-button" href="#destinations">Explore destinations</a>
            </div>
          </div>
          <div className="hero-map" aria-label="Route preview">
            <span className="route-dot dot-a" />
            <span className="route-dot dot-b" />
            <span className="route-dot dot-c" />
            <svg viewBox="0 0 260 180">
              <path d="M35 128 C80 50, 126 154, 166 82 S225 65, 235 134" />
            </svg>
          </div>
        </section>

        <div className="content-grid">
          <section id="destinations" className="panel destination-panel">
            <div className="panel-header">
              <div>
                <h2>Destination shelf</h2>
                <p>{notice}</p>
              </div>
              <button className="compact-button" onClick={handleSearch}>Refresh</button>
            </div>
            <div className="destination-grid">
              {destinations.map((destination) => (
                <article className="destination-card" key={destination.id}>
                  <img src={destination.coverImageUrl} alt="" />
                  <div>
                    <span>{destination.regionName || destination.regionCode || "Scenic stop"}</span>
                    <h3>{destination.name}</h3>
                    <p>{destination.summary}</p>
                    <button onClick={() => handleAddDestination(destination)}>
                      <Icon name="plus" />Add stop
                    </button>
                  </div>
                </article>
              ))}
            </div>
          </section>

          <section id="planner" className="panel planner-panel">
            <div className="panel-header">
              <div>
                <h2>Itinerary builder</h2>
                <p>{selectedItinerary?.id === "draft" ? "Draft locally until you sign in." : `Live trip #${selectedItinerary?.id}`}</p>
              </div>
            </div>

            {!user ? (
              <form className="auth-card" onSubmit={handleAuth}>
                <div className="auth-tabs">
                  <button type="button" className={authMode === "login" ? "selected" : ""} onClick={() => setAuthMode("login")}>Login</button>
                  <button type="button" className={authMode === "register" ? "selected" : ""} onClick={() => setAuthMode("register")}>Register</button>
                </div>
                <label>
                  Username
                  <input value={authForm.username} onChange={(event) => setAuthForm({ ...authForm, username: event.target.value })} />
                </label>
                <label>
                  Password
                  <input type="password" value={authForm.password} onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })} />
                </label>
                {authMode === "register" ? (
                  <label>
                    Nickname
                    <input value={authForm.nickname} onChange={(event) => setAuthForm({ ...authForm, nickname: event.target.value })} />
                  </label>
                ) : null}
                <button className="primary-button" type="submit">{authMode === "register" ? "Register and login" : "Login to test"}</button>
              </form>
            ) : (
              <div className="trip-form">
                <label>
                  Trip title
                  <input value={draftTitle} onChange={(event) => setDraftTitle(event.target.value)} />
                </label>
                <label>
                  Description
                  <textarea value={draftDescription} onChange={(event) => setDraftDescription(event.target.value)} />
                </label>
              </div>
            )}

            <div className="timeline">
              {(selectedItinerary?.destinations?.length ? selectedItinerary.destinations : selectedDestinations).map((stop, index) => (
                <div className="timeline-row" key={`${stop.destinationId || stop.id}-${index}`}>
                  <span className="timeline-index">{index + 1}</span>
                  <div>
                    <h3>{stop.name}</h3>
                    <p>{stop.summary || "A calm stop waiting for more detail."}</p>
                  </div>
                  <button onClick={() => handleRemoveDestination(stop.destinationId || stop.id)}>Remove</button>
                </div>
              ))}
            </div>
          </section>

          <aside className="panel summary-panel">
            <h2>Route summary</h2>
            <div className="summary-stat">
              <strong>{(selectedItinerary?.destinations || selectedDestinations).length}</strong>
              <span>planned stops</span>
            </div>
            <div className="summary-stat">
              <strong>{user ? "Live" : "Draft"}</strong>
              <span>gateway mode</span>
            </div>
            <div className="debug-chip">test env: localhost:8080</div>
            <div className="stop-list">
              {(selectedItinerary?.destinations || selectedDestinations).map((stop, index) => (
                <span key={`${stop.destinationId || stop.id}-summary`}>{index + 1}. {stop.name}</span>
              ))}
            </div>
          </aside>
        </div>
      </section>
    </main>
  );
}

createRoot(document.getElementById("root")).render(<App />);
