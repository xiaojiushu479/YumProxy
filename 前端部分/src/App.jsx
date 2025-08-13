import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Layout } from 'antd';
import UserApp from './UserApp';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <Layout style={{ minHeight: '100vh' }}>
        <Layout.Content>
          <Routes>
            <Route path="/*" element={<UserApp />} />
          </Routes>
        </Layout.Content>
      </Layout>
    </BrowserRouter>
  );
}
