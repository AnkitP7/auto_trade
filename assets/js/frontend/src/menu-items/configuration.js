// assets
import { ControlOutlined, StockOutlined, SettingOutlined } from '@ant-design/icons';

// icons
const icons = {
  ControlOutlined,
  StockOutlined,
  SettingOutlined
};


const configuration = {
  id: 'configuration',
  title: 'Options',
  type: 'group',
  children: [
    {
      id: 'configuration',
      title: 'Configuration',
      type: 'item',
      url: '/configuration',
      icon: icons.ControlOutlined
    },
    {
      id: 'strategy',
      title: 'Strategy',
      type: 'item',
      url: '/strategy',
      icon: icons.StockOutlined,
      // external: false,
      // target: false
    },
    {
      id: 'settings',
      title: 'Settings',
      type: 'item',
      url: '/settings',
      icon: icons.SettingOutlined,
    }
  ]
};

export default configuration;
