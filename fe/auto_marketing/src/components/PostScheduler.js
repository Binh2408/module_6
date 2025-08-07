// components/PostScheduler.js
import React, { useState } from 'react';
import axios from 'axios';

const PostScheduler = ({ page }) => {
  const [message, setMessage] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [scheduledTime, setScheduledTime] = useState('');
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSchedulePost = async () => {
    if (!message || !scheduledTime) {
      setStatus('⚠️ Vui lòng điền nội dung và thời gian đăng.');
      return;
    }

    setLoading(true);
    setStatus('📤 Đang gửi thông tin lên server...');

    try {
      await axios.post('http://localhost:8080/api/schedule/dto', {
        pageId: page.id,
        accessToken: page.access_token,
        message,
        imageUrl,
        scheduledTime,
        userId:1
      });

      setStatus('✅ Đã đặt lịch đăng bài thành công!');
      setMessage('');
      setImageUrl('');
      setScheduledTime('');
    } catch (error) {
      console.error(error);
      setStatus('❌ Lỗi khi đặt lịch đăng.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ marginTop: '30px' }}>
      <h3>📅 Đặt lịch bài viết cho: {page.name}</h3>

      <div>
        <label>Nội dung:</label><br />
        <textarea
          rows={4}
          cols={50}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
      </div>

      <div>
        <label>URL ảnh (tùy chọn):</label><br />
        <input
          type="text"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
        />
      </div>

      <div>
        <label>Thời gian đăng (YYYY-MM-DDTHH:mm):</label><br />
        <input
          type="datetime-local"
          value={scheduledTime}
          onChange={(e) => setScheduledTime(e.target.value)}
        />
      </div>

      <button disabled={loading} onClick={handleSchedulePost}>
        📅 Đặt lịch đăng
      </button>

      {status && <p>{status}</p>}
    </div>
  );
};

export default PostScheduler;
