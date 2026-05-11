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
  searchDestinations,
  updateItinerary
} from "./api";
import "./styles.css";

const demoCredentials = {
  username: "traveler_test",
  password: "travel123",
  nickname: "FanOne"
};

const DEFAULT_DESTINATION_QUERY = "a";

function Icon({ name }) {
  const paths = {
    compass: "M12 2l3 7 7 3-7 3-3 7-3-7-7-3 7-3 3-7Zm0 7a3 3 0 1 0 0 6 3 3 0 0 0 0-6Z",
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
  const [destinations, setDestinations] = useState([]);
  const [itineraries, setItineraries] = useState([]);
  const [selectedItineraryId, setSelectedItineraryId] = useState("");
  const [keyword, setKeyword] = useState("");
  const [draftTitle, setDraftTitle] = useState("");
  const [draftDescription, setDraftDescription] = useState("");
  const [selectedDestinationIds, setSelectedDestinationIds] = useState([]);
  const [notice, setNotice] = useState(
    initialSession.token ? "正在连接测试环境网关。" : "请登录后加载真实目的地和行程。"
  );
  const [isPending, startTransition] = useTransition();

  const selectedItinerary = useMemo(
    () => itineraries.find((item) => String(item.id) === String(selectedItineraryId)) || null,
    [itineraries, selectedItineraryId]
  );

  const visibleStops = selectedItinerary?.destinations?.length
    ? selectedItinerary.destinations
    : destinations.filter((destination) => selectedDestinationIds.includes(destination.id));

  useEffect(() => {
    if (!initialSession.token) {
      return;
    }

    getCurrentUser()
      .then((currentUser) => {
        setUser(currentUser);
        return refreshWorkspace(DEFAULT_DESTINATION_QUERY);
      })
      .catch(() => {
        clearSession();
        setUser(null);
        setNotice("登录已失效，请重新登录。");
      });
  }, []);

  async function refreshWorkspace(nextKeyword = DEFAULT_DESTINATION_QUERY) {
    const query = nextKeyword.trim() || DEFAULT_DESTINATION_QUERY;
    const [destinationData, itineraryData] = await Promise.all([
      searchDestinations(query),
      listItineraries()
    ]);

    startTransition(() => {
      setDestinations(destinationData);
      setItineraries(itineraryData);

      if (itineraryData.length > 0) {
        const firstItinerary = itineraryData[0];
        setSelectedItineraryId(firstItinerary.id);
        setDraftTitle(firstItinerary.title);
        setDraftDescription(firstItinerary.description || "");
        setSelectedDestinationIds((firstItinerary.destinations || []).map((item) => item.destinationId));
      } else {
        setSelectedItineraryId("");
        setDraftTitle("");
        setDraftDescription("");
        setSelectedDestinationIds([]);
      }

      setNotice(`已从网关加载 ${destinationData.length} 个目的地。`);
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
      await refreshWorkspace(DEFAULT_DESTINATION_QUERY);
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleSearch(event) {
    event.preventDefault();
    if (!user) {
      setNotice("请先登录。");
      return;
    }

    if (!keyword.trim()) {
      await refreshWorkspace(DEFAULT_DESTINATION_QUERY);
      return;
    }

    try {
      const data = await searchDestinations(keyword);
      setDestinations(data);
      setNotice(`搜索完成，找到 ${data.length} 个目的地。`);
    } catch (error) {
      setNotice(error.message);
      setDestinations([]);
    }
  }

  async function handleCreateItinerary() {
    if (!user) {
      setNotice("请先登录。");
      return;
    }

    if (!draftTitle.trim()) {
      setNotice("请填写行程标题。");
      return;
    }

    try {
      const response = await createItinerary({
        title: draftTitle.trim(),
        description: draftDescription.trim(),
        destinationIds: selectedDestinationIds
      });
      setItineraries([response, ...itineraries]);
      setSelectedItineraryId(response.id);
      setSelectedDestinationIds((response.destinations || []).map((item) => item.destinationId));
      setNotice("行程已创建。");
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleSaveItinerary() {
    if (!selectedItinerary) {
      await handleCreateItinerary();
      return;
    }

    if (!draftTitle.trim()) {
      setNotice("请填写行程标题。");
      return;
    }

    try {
      const updated = await updateItinerary(selectedItinerary.id, {
        title: draftTitle.trim(),
        description: draftDescription.trim()
      });
      setItineraries(itineraries.map((item) => (item.id === updated.id ? updated : item)));
      setNotice("行程已保存。");
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleAddDestination(destination) {
    if (!user) {
      setNotice("请先登录。");
      return;
    }

    if (selectedDestinationIds.includes(destination.id)) {
      setNotice("该目的地已经在当前行程中。");
      return;
    }

    if (!selectedItinerary) {
      setSelectedDestinationIds([...selectedDestinationIds, destination.id]);
      setNotice("已加入待创建行程。");
      return;
    }

    try {
      const updated = await addDestinationToItinerary(selectedItinerary.id, destination.id);
      setItineraries(itineraries.map((item) => (item.id === updated.id ? updated : item)));
      setSelectedDestinationIds((updated.destinations || []).map((item) => item.destinationId));
      setNotice(`已将 ${destination.name} 加入行程。`);
    } catch (error) {
      setNotice(error.message);
    }
  }

  async function handleRemoveDestination(destinationId) {
    if (!selectedItinerary) {
      setSelectedDestinationIds(selectedDestinationIds.filter((id) => id !== destinationId));
      setNotice("已从待创建行程移除。");
      return;
    }

    try {
      const updated = await removeDestinationFromItinerary(selectedItinerary.id, destinationId);
      setItineraries(itineraries.map((item) => (item.id === updated.id ? updated : item)));
      setSelectedDestinationIds((updated.destinations || []).map((item) => item.destinationId));
      setNotice("站点已移除。");
    } catch (error) {
      setNotice(error.message);
    }
  }

  function handleLogout() {
    clearSession();
    setUser(null);
    setDestinations([]);
    setItineraries([]);
    setSelectedItineraryId("");
    setSelectedDestinationIds([]);
    setDraftTitle("");
    setDraftDescription("");
    setNotice("已退出登录。");
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div className="brand-mark">
          <span className="brand-symbol">旅</span>
          <span>路线灯塔</span>
        </div>
        <form className="search-box" onSubmit={handleSearch}>
          <Icon name="search" />
          <input
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="搜索目的地名称"
            aria-label="搜索目的地名称"
            disabled={!user}
          />
        </form>
        <div className="user-area">
          <span>{user ? `你好，${user.nickname || user.username}` : "未登录"}</span>
          {user ? (
            <button className="ghost-button" onClick={handleLogout}>退出</button>
          ) : null}
        </div>
      </header>

      <section className="notice-bar" aria-live="polite">{notice}</section>

      {!user ? (
        <section className="auth-layout">
          <form className="panel auth-card" onSubmit={handleAuth}>
            <div className="panel-header">
              <div>
                <h1>登录测试环境</h1>
                <p>登录后加载网关返回的目的地和行程数据。</p>
              </div>
            </div>
            <div className="auth-tabs">
              <button type="button" className={authMode === "login" ? "selected" : ""} onClick={() => setAuthMode("login")}>登录</button>
              <button type="button" className={authMode === "register" ? "selected" : ""} onClick={() => setAuthMode("register")}>注册</button>
            </div>
            <label>
              用户名
              <input value={authForm.username} onChange={(event) => setAuthForm({ ...authForm, username: event.target.value })} />
            </label>
            <label>
              密码
              <input type="password" value={authForm.password} onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })} />
            </label>
            {authMode === "register" ? (
              <label>
                昵称
                <input value={authForm.nickname} onChange={(event) => setAuthForm({ ...authForm, nickname: event.target.value })} />
              </label>
            ) : null}
            <button className="primary-button" type="submit">{authMode === "register" ? "注册并登录" : "登录"}</button>
          </form>
        </section>
      ) : (
        <div className="content-grid">
          <section className="panel destination-panel">
            <div className="panel-header">
              <div>
                <h1>目的地</h1>
                <p>只显示网关返回的数据。</p>
              </div>
              <button className="compact-button" onClick={() => refreshWorkspace(DEFAULT_DESTINATION_QUERY)} disabled={isPending}>
                刷新
              </button>
            </div>
            <div className="destination-grid">
              {destinations.length > 0 ? destinations.map((destination) => (
                <article className="destination-card" key={destination.id}>
                  <img src={destination.coverImageUrl} alt="" />
                  <div>
                    <span>{destination.regionName || destination.regionCode}</span>
                    <h2>{destination.name}</h2>
                    <p>{destination.summary}</p>
                    <button onClick={() => handleAddDestination(destination)}>
                      <Icon name="plus" />加入行程
                    </button>
                  </div>
                </article>
              )) : (
                <p className="empty-state">没有目的地数据。请调整关键词后搜索。</p>
              )}
            </div>
          </section>

          <section className="panel planner-panel">
            <div className="panel-header">
              <div>
                <h1>行程</h1>
                <p>{selectedItinerary ? `正在编辑行程 #${selectedItinerary.id}` : "创建一个新行程。"}</p>
              </div>
            </div>
            <div className="trip-form">
              <label>
                行程标题
                <input value={draftTitle} onChange={(event) => setDraftTitle(event.target.value)} />
              </label>
              <label>
                行程描述
                <textarea value={draftDescription} onChange={(event) => setDraftDescription(event.target.value)} />
              </label>
              <button className="primary-button" onClick={handleSaveItinerary} disabled={isPending}>
                {selectedItinerary ? "保存行程" : "创建行程"}
              </button>
            </div>

            <div className="timeline">
              {visibleStops.length > 0 ? visibleStops.map((stop, index) => (
                <div className="timeline-row" key={`${stop.destinationId || stop.id}-${index}`}>
                  <span className="timeline-index">{index + 1}</span>
                  <div>
                    <h2>{stop.name}</h2>
                    <p>{stop.summary || "暂无摘要。"}</p>
                  </div>
                  <button onClick={() => handleRemoveDestination(stop.destinationId || stop.id)}>移除</button>
                </div>
              )) : (
                <p className="empty-state">还没有加入任何站点。</p>
              )}
            </div>
          </section>

          <aside className="panel summary-panel">
            <h1>摘要</h1>
            <div className="summary-stat">
              <strong>{visibleStops.length}</strong>
              <span>站点</span>
            </div>
            <div className="summary-stat">
              <strong>{selectedItinerary ? "已保存" : "新建"}</strong>
              <span>状态</span>
            </div>
            <div className="stop-list">
              {visibleStops.map((stop, index) => (
                <span key={`${stop.destinationId || stop.id}-summary`}>{index + 1}. {stop.name}</span>
              ))}
            </div>
          </aside>
        </div>
      )}
    </main>
  );
}

createRoot(document.getElementById("root")).render(<App />);
