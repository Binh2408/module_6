import React, { useEffect, useState } from "react";

function SchedulePost() {
  const [pages, setPages] = useState([]);
  const [pageId, setPageId] = useState("");
  const [message, setMessage] = useState("");
  const [scheduledTime, setScheduledTime] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/facebook/pages", {
      credentials: "include",
    })
      .then((res) => res.json())
      .then((data) => {
        setPages(data.data || []);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!pageId || !message || !scheduledTime) {
      alert("Vui lòng điền đầy đủ thông tin");
      return;
    }

    fetch("http://localhost:8080/api/schedule/create", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ pageId, message, scheduledTime }),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.message) {
          alert(data.message);
          setPageId("");
          setMessage("");
          setScheduledTime("");
        } else if (data.error) {
          alert("Lỗi: " + data.error);
        }
      })
      .catch(() => alert("Lỗi kết nối"));
  };

  if (loading) return <p>Đang tải danh sách trang...</p>;

  return (
    <form onSubmit={handleSubmit}>
      <h2>Đặt lịch đăng bài lên trang Facebook</h2>

      <label>
        Chọn trang:
        <select value={pageId} onChange={(e) => setPageId(e.target.value)} required>
          <option value="">-- Chọn trang --</option>
          {pages.map((page) => (
            <option key={page.pageId} value={page.pageId}>
              {page.pageName}
            </option>
          ))}
        </select>
      </label>
      <br />

      <label>
        Nội dung bài đăng:
        <textarea value={message} onChange={(e) => setMessage(e.target.value)} required />
      </label>
      <br />

      <label>
        Thời gian đăng:
        <input
          type="datetime-local"
          value={scheduledTime}
          onChange={(e) => setScheduledTime(e.target.value)}
          required
        />
      </label>
      <br />

      <button type="submit">Đặt lịch</button>
    </form>
  );
}

export default SchedulePost;
