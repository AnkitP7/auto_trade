import { lazy } from 'react';

// project import
import Loadable from 'components/Loadable';
import MainLayout from 'layout/MainLayout';

// render - dashboard
const DashboardDefault = Loadable(lazy(() => import('pages/dashboard')));

const Configuration = Loadable(lazy(() => import('pages/extra-pages/configuration')));
const Strategy = Loadable(lazy(() => import('pages/extra-pages/strategy')));
const Settings = Loadable(lazy(() => import('pages/extra-pages/settings')));


// ==============================|| MAIN ROUTING ||============================== //

const MainRoutes = {
  path: '/',
  element: <MainLayout />,
  children: [
    {
      path: '/default',
      element: <DashboardDefault />
    },
    {
      path: 'dashboard',
      children: [
        {
          path: 'default',
          element: <DashboardDefault />
        }
      ]
    },
    {
      path: 'configuration',
      element: <Configuration />
    },
    {
      path: 'strategy',
      element: <Strategy />
    },
    {
      path: 'settings',
      element: <Settings />
    }
  ]
};

export default MainRoutes;
