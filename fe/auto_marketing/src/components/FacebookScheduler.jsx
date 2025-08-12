// FacebookScheduler.jsx
import React, { useEffect, useState } from "react";
import ScheduleForm from "./ScheduleForm";
import ScheduleList from "./ScheduleList";

function FacebookScheduler() {
  const [pages, setPages] = useState([]);
  const [loadingPages, setLoadingPages] = useState(true);
  const [error, setError] = useState(null);
  const [refreshSchedulesFlag, setRefreshSchedulesFlag] = useState(false);

  // Lấy pages đã lưu trong DB
  const fetchPages = async () => {
    setLoadingPages(true);
    setError(null);
    try {
      const res = await fetch("http://localhost:8080/api/facebook/pages", {
        credentials: "include",
      });
      if (!res.ok) {
        // thân thiện: nếu response body JSON, try parse for message
        let txt = await res.text();
        try { const j = JSON.parse(txt); throw new Error(j.error || JSON.stringify(j)); }
        catch (e) { throw new Error(txt || "Failed to fetch pages"); }
      }
      const data = await res.json();
      // backend trả kiểu { data: [...] } theo mẫu trước
      setPages(data.data || data.pages || []);
    } catch (err) {
      setError(err.message || "Lỗi khi lấy pages");
    } finally {
      setLoadingPages(false);
    }
  };

  useEffect(() => {
    fetchPages();
  }, []);

  // callback để refresh schedules list sau tạo/update/delete
  const onSchedulesChanged = () => {
    setRefreshSchedulesFlag((f) => !f);
  };

  return (
    <div style={{ maxWidth: 900, margin: "24px auto", padding: 16 }}>
      <h1>Quản lý lịch đăng Facebook</h1>

      {error && <div style={{ color: "red", marginBottom: 12 }}>Lỗi: {error}</div>}

      <section style={{ marginBottom: 24 }}>
        <h2>Trang Facebook đã lưu</h2>
        {loadingPages ? (
          <p>Đang tải trang...</p>
        ) : pages.length === 0 ? (
          <p>Chưa có trang nào được lưu. Hãy login và lưu pages trước.</p>
        ) : (
          <ul>
            {pages.map((p) => (
              <li key={p.pageId}>
                <strong>{p.pageName}</strong> (ID: {p.pageId})
              </li>
            ))}
          </ul>
        )}
      </section>

      <section style={{ marginBottom: 24 }}>
        <h2>Tạo lịch đăng mới</h2>
        <ScheduleForm pages={pages} onCreated={onSchedulesChanged} />
      </section>

      <section>
        <h2>Danh sách lịch của bạn</h2>
        <ScheduleList refreshFlag={refreshSchedulesFlag} onChanged={onSchedulesChanged} />
      </section>
    </div>
  );
}

export default FacebookScheduler;
