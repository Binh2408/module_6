// ScheduleList.jsx
import React, { useEffect, useState } from "react";
import EditScheduleModal from "./EditScheduleModal";

function ScheduleList({ refreshFlag = false, onChanged = () => {} }) {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editItem, setEditItem] = useState(null);

  const fetchSchedules = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch("http://localhost:8080/api/schedules", {
        credentials: "include",
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Failed to load");
      }
      const j = await res.json();
      // backend returns { data: [...] } or direct array
      setSchedules(j.data || j.schedules || j);
    } catch (err) {
      setError(err.message || "Lỗi");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchedules();
    // eslint-disable-next-line
  }, [refreshFlag]);

  const handleDelete = async (id) => {
    if (!window.confirm("Xác nhận xóa lịch này?")) return;
    try {
      const res = await fetch(`http://localhost:8080/api/schedules/${id}`, {
        method: "DELETE",
        credentials: "include",
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Delete failed");
      }
      onChanged();
      fetchSchedules();
    } catch (err) {
      alert("Lỗi: " + (err.message || "Xóa thất bại"));
    }
  };

  if (loading) return <p>Đang tải lịch...</p>;
  if (error) return <p style={{ color: "red" }}>Lỗi: {error}</p>;
  if (!schedules || schedules.length === 0) return <p>Chưa có lịch nào.</p>;

  return (
    <div>
      <ul>
        {schedules.map((s) => (
          <li key={s.id} style={{ marginBottom: 12, border: "1px solid #eee", padding: 8 }}>
            <div><strong>Trang:</strong> {s.page?.pageName || s.pageName} (ID: {s.page?.pageId || s.pageId})</div>
            <div><strong>Nội dung:</strong> {s.message}</div>
            {s.imageUrl && <div><strong>Ảnh:</strong> <a href={s.imageUrl} target="_blank" rel="noreferrer">Link</a></div>}
            <div><strong>Thời gian:</strong> {s.scheduledTime}</div>
            <div><strong>Trạng thái:</strong> {s.status}</div>

            <div style={{ marginTop: 8 }}>
              {s.status === "PENDING" && (
                <>
                  <button onClick={() => setEditItem(s)}>Sửa</button>
                  <button onClick={() => handleDelete(s.id)} style={{ marginLeft: 8 }}>Xóa</button>
                </>
              )}
              <button onClick={() => {
                // có thể show logs – hiện simple alert for now
                fetchLogs(s.id);
              }} style={{ marginLeft: 8 }}>Xem log</button>
            </div>
          </li>
        ))}
      </ul>

      {editItem && (
        <EditScheduleModal
          item={editItem}
          onClose={() => setEditItem(null)}
          onSaved={() => { setEditItem(null); onChanged(); fetchSchedules(); }}
        />
      )}
    </div>
  );

  async function fetchLogs(scheduledId) {
    try {
      const res = await fetch(`http://localhost:8080/api/schedules/logs/${scheduledId}`, {
        credentials: "include",
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Fetch logs failed");
      }
      const j = await res.json();
      const logs = j.data || j;
      if (!logs || logs.length === 0) {
        alert("Chưa có log nào.");
        return;
      }
      alert(logs.map(l => `${l.attemptedAt}: ${l.success ? "OK" : "ERR"} - ${l.result}`).join("\n\n"));
    } catch (err) {
      alert("Lỗi khi lấy log: " + (err.message || ""));
    }
  }
}

export default ScheduleList;
