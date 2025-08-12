// EditScheduleModal.jsx
import React, { useState } from "react";

function EditScheduleModal({ item, onClose = () => {}, onSaved = () => {} }) {
  const [message, setMessage] = useState(item.message || "");
  const [imageUrl, setImageUrl] = useState(item.imageUrl || "");
  const [scheduledAt, setScheduledAt] = useState(localDatetimeFromISO(item.scheduledTime));
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  function localDatetimeFromISO(iso) {
    if (!iso) return "";
    // iso like "2025-08-12T15:30:00Z" -> remove Z and offset safely
    const d = new Date(iso);
    const pad = (n) => String(n).padStart(2, "0");
    const local = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    return local;
  }

  const toISO = (localDatetime) => {
    if (!localDatetime) return null;
    return new Date(localDatetime).toISOString();
  };

  const handleSave = async () => {
    setError(null);
    setLoading(true);
    try {
      const payload = {
        id: item.id,
        message,
        imageUrl,
        scheduledTime: toISO(scheduledAt)
      };
      const res = await fetch("http://localhost:8080/api/schedules", {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Update failed");
      }
      await res.json();
      onSaved();
    } catch (err) {
      setError(err.message || "Lỗi");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      position: "fixed", left:0, right:0, top:0, bottom:0, background:"rgba(0,0,0,0.3)",
      display:"flex", alignItems:"center", justifyContent:"center"
    }}>
      <div style={{ background:"#fff", padding:16, width:600, borderRadius:6 }}>
        <h3>Sửa lịch</h3>
        <div>
          <label>Nội dung</label><br />
          <textarea rows={4} value={message} onChange={(e)=>setMessage(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div>
          <label>URL ảnh</label><br />
          <input value={imageUrl} onChange={(e)=>setImageUrl(e.target.value)} style={{ width: "100%" }} />
        </div>
        <div>
          <label>Thời gian</label><br />
          <input type="datetime-local" value={scheduledAt} onChange={(e)=>setScheduledAt(e.target.value)} />
        </div>

        {error && <div style={{ color:"red" }}>{error}</div>}

        <div style={{ marginTop: 12 }}>
          <button onClick={handleSave} disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</button>
          <button onClick={onClose} style={{ marginLeft: 8 }}>Đóng</button>
        </div>
      </div>
    </div>
  );
}

export default EditScheduleModal;
