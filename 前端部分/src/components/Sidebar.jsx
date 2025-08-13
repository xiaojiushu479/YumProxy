import { Layout, Menu } from 'antd';
const { Sider } = Layout;

export default function Sidebar({ onSelect }) {
  return (
    <Sider>
      <Menu
        theme="dark"
        mode="inline"
        defaultSelectedKeys={['home']}
        onClick={({ key }) => onSelect(key)}
        items={[
          { key: 'home', label: '濡絾鐗犻妴? },
          { key: 'purchase', label: '閻犳劦鍘洪幏? },
          { key: 'payment', label: '闁衡偓椤栨瑧甯? },
          { key: 'card', label: '闁告せ鈧磭妲? },
          { key: 'time', label: '闁哄啫鐖奸弳? },
          { key: 'id', label: 'ID' },
        ]}
      />
    </Sider>
  );
}
