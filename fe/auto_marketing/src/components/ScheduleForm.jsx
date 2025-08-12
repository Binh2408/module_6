// ScheduleForm.jsx
import React, { useState } from "react";

/**
 * props:
 * - pages: array of { pageId, pageName, pageAccessToken, owner... }
 * - onCreated: callback when created
 */
function ScheduleForm({ pages = [], onCreated = () => {} }) {
  const [pageId, setPageId] = useState(pages.length ? pages[0].pageId : "");
  const [message, setMessage] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [scheduledAt, setScheduledAt] = useState(""); // datetime-local value
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // update selected when pages prop changes
  React.useEffect(() => {
    if (pages.length && !pageId) setPageId(pages[0].pageId);
  }, [pages, pageId]);

  const toISO = (localDatetime) => {
    // localDatetime is from <input type="datetime-local"> like "2025-08-12T15:30"
    if (!localDatetime) return null;
    const dt = new Date(localDatetime);
    // return ISO string without timezone offset handled by backend. Use toISOString()
    return dt.toISOString();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!pageId) return setError("Vui lòng chọn trang");
    if (!message && !imageUrl) return setError("Vui lòng nhập nội dung hoặc ảnh");
    if (!scheduledAt) return setError("Vui lòng chọn thời gian");

    setLoading(true);
    try {
      const payload = {
        pageId,
        message,
        imageUrl,
        scheduledTime: toISO(scheduledAt)
      };
      const res = await fetch("http://localhost:8080/api/schedules", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const txt = await res.text();
        try { const j = JSON.parse(txt); throw new Error(j.message || j.error || txt); }
        catch (e) { throw new Error(txt || "Create failed"); }
      }
      const j = await res.json();
      onCreated();
      // reset form
      setMessage("");
      setImageUrl("");
      setScheduledAt("");
      alert("Tạo lịch thành công");
    } catch (err) {
      setError(err.message || "Lỗi khi tạo lịch");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ border: "1px solid #eee", padding: 12, borderRadius: 6 }}>
      <div style={{ marginBottom: 8 }}>
        <label>Chọn trang: </label>
        <select value={pageId} onChange={(e) => setPageId(e.target.value)}>
          <option value="">-- Chọn trang --</option>
          {pages.map((p) => (
            <option key={p.pageId} value={p.pageId}>
              {p.pageName} ({p.pageId})
            </option>
          ))}
        </select>
      </div>

      <div style={{ marginBottom: 8 }}>
        <label>Nội dung</label><br />
        <textarea value={message} onChange={(e) => setMessage(e.target.value)} rows={4} style={{ width: "100%" }} />
      </div>

      <div style={{ marginBottom: 8 }}>
        <label>URL ảnh (nếu có)</label><br />
        <input value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} style={{ width: "100%" }} />
      </div>

      <div style={{ marginBottom: 8 }}>
        <label>Thời gian (local)</label><br />
        <input
          type="datetime-local"
          value={scheduledAt}
          onChange={(e) => setScheduledAt(e.target.value)}
        />
      </div>

      {error && <div style={{ color: "red", marginBottom: 8 }}>{error}</div>}

      <button type="submit" disabled={loading}>
        {loading ? "Đang tạo..." : "Tạo lịch"}
      </button>
    </form>
  );
}

export default ScheduleForm;
