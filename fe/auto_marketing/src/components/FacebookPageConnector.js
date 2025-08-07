// components/FacebookPageConnector.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import PostScheduler from './PostScheduler';

const FacebookPageConnector = () => {
  const [pages, setPages] = useState([]);
  const [selectedPage, setSelectedPage] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!window.FB) {
      window.fbAsyncInit = function () {
        window.FB.init({
          appId: '1288979379905002',
          cookie: true,
          xfbml: true,
          version: 'v19.0'
        });
      };
      const script = document.createElement('script');
      script.src = 'https://connect.facebook.net/en_US/sdk.js';
      script.async = true;
      script.defer = true;
      document.body.appendChild(script);
    }
  }, []);

  const handleFacebookLogin = () => {
    window.FB.login((response) => {
      if (response.authResponse) {
        window.FB.api('/me/accounts', (res) => {
          if (res && !res.error) {
            setPages(res.data);
            setMessage('🎉 Đăng nhập thành công! Chọn trang bên dưới.');
          } else {
            setMessage('❌ Lỗi khi lấy danh sách trang.');
            console.error(res.error);
          }
        });
      } else {
        setMessage('❌ Hủy đăng nhập hoặc không cấp quyền.');
      }
    }, {
      scope: 'pages_show_list,pages_read_engagement,pages_manage_posts,pages_manage_metadata',
    });
  };

  const handlePageSelect = (page) => setSelectedPage(page);

  const handleSavePageToBackend = async () => {
    if (!selectedPage) {
      setMessage('⚠️ Vui lòng chọn một page.');
      return;
    }

    try {
      await axios.post('http://localhost:8080/api/facebook-pages/save', {
        pageId: selectedPage.id,
        accessToken: selectedPage.access_token,
        name: selectedPage.name
      });
      setMessage('✅ Đã lưu thông tin page thành công!');
    } catch (error) {
      console.error(error);
      if (error.response) {
        setMessage('❌ Lỗi lưu thông tin page: ' + error.response.data);
      } else {
        setMessage('❌ Lỗi mạng hoặc server.');
      }
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Kết nối Fanpage Facebook</h2>
      <button onClick={handleFacebookLogin}>🔑 Đăng nhập Facebook</button>

      {pages.length > 0 && (
        <>
          <h3>📄 Danh sách trang:</h3>
          <ul>
            {pages.map((page) => (
              <li key={page.id}>
                <label>
                  <input
                    type="radio"
                    name="selectedPage"
                    value={page.id}
                    onChange={() => handlePageSelect(page)}
                  />
                  {page.name} ({page.id})
                </label>
              </li>
            ))}
          </ul>
          <button
            disabled={!selectedPage}
            onClick={handleSavePageToBackend}
          >
            💾 Lưu thông tin page
          </button>
        </>
      )}

      {message && <p>{message}</p>}

      {/* 👉 Gửi page info sang component đặt lịch */}
      {selectedPage && (
        <PostScheduler page={selectedPage} />
      )}
    </div>
  );
};

export default FacebookPageConnector;
