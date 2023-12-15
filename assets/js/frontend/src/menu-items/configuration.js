// assets
import { ChromeOutlined, QuestionOutlined } from '@ant-design/icons';

// icons
const icons = {
  ChromeOutlined,
  QuestionOutlined
};


const configuration = {
  id: 'configuration',
  title: 'Trade Configuration',
  type: 'group',
  children: [
    {
      id: 'configuration',
      title: 'Configuration',
      type: 'item',
      url: '/configuration',
      icon: icons.ChromeOutlined
    },
    {
      id: 'strategy',
      title: 'Strategy',
      type: 'item',
      url: '/strategy',
      icon: icons.QuestionOutlined,
      // external: false,
      // target: false
    }
  ]
};

export default configuration;
