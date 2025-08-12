import React, { useEffect, useState } from "react";

function FacebookPagesManager() {
  const [livePages, setLivePages] = useState([]);
  const [savedPages, setSavedPages] = useState([]);
  const [loadingLive, setLoadingLive] = useState(true);
  const [loadingSaved, setLoadingSaved] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // Lấy pages live từ FB API (backend proxy)
  const fetchLivePages = () => {
    setLoadingLive(true);
    fetch("http://localhost:8080/api/facebook/pages-list", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Lỗi khi lấy trang từ Facebook");
        return res.json();
      })
      .then((data) => {
        setLivePages(data.data || []);
        setLoadingLive(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoadingLive(false);
      });
  };

  // Lấy pages đã lưu trong DB
  const fetchSavedPages = () => {
    setLoadingSaved(true);
    fetch("http://localhost:8080/api/facebook/pages", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Lỗi khi lấy trang đã lưu");
        return res.json();
      })
      .then((data) => {
        setSavedPages(data.data || []);
        setLoadingSaved(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoadingSaved(false);
      });
  };

  useEffect(() => {
    fetchLivePages();
    fetchSavedPages();
  }, []);

  // Hàm gọi backend lưu pages live vào DB
  const savePagesToDB = () => {
  if (livePages.length === 0) return;
  setSaving(true);

  // Chuyển dữ liệu từ FB API sang format backend cần
  const pagesToSave = livePages.map(page => ({
    pageId: page.id,
    pageName: page.name,
    pageAccessToken: page.access_token, // đổi key cho khớp backend
    ownerId: 1 // hoặc lấy từ session user
  }));

  fetch("http://localhost:8080/api/facebook/pages/save", {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(pagesToSave),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Lỗi khi lưu trang vào DB");
      return res.json();
    })
    .then(() => {
      setSaving(false);
      fetchSavedPages(); // cập nhật lại danh sách đã lưu
    })
    .catch((err) => {
      setError(err.message);
      setSaving(false);
    });
};


  return (
    <div>
      <h2>Danh sách trang Facebook live từ Facebook API</h2>
      {loadingLive ? (
        <p>Đang tải trang live...</p>
      ) : livePages.length === 0 ? (
        <p>Bạn chưa quản lý trang nào hoặc chưa cấp quyền.</p>
      ) : (
        <>
          <ul>
            {livePages.map((page) => (
              <li key={page.id}>
                {page.name} (ID: {page.id})
              </li>
            ))}
          </ul>
          <button onClick={savePagesToDB} disabled={saving}>
            {saving ? "Đang lưu..." : "Lưu các trang này vào hệ thống"}
          </button>
        </>
      )}

      <hr />

      <h2>Danh sách các trang Facebook đã lưu trong DB</h2>
      {loadingSaved ? (
        <p>Đang tải trang đã lưu...</p>
      ) : savedPages.length === 0 ? (
        <p>Chưa có trang nào được lưu.</p>
      ) : (
        <ul>
          {savedPages.map((page) => (
            <li key={page.pageId}>
              {page.pageName} (ID: {page.pageId})
            </li>
          ))}
        </ul>
      )}

      {error && <p style={{ color: "red" }}>Lỗi: {error}</p>}
    </div>
  );
}

export default FacebookPagesManager;
