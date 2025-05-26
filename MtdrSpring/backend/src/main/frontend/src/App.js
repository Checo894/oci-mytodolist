import React from "react";
import { BrowserRouter as Router, Route, Switch, Redirect } from "react-router-dom";
import LoginPage from "./views/LoginPage";
// Los siguientes componentes serán creados después
import DeveloperView from "./views/DeveloperView";
import ManagerView from "./views/ManagerView";
import TaskDetailView from "./components/TaskDetailView";
import SprintDetailView from "./components/SprintDetailView";

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/">
          <Redirect to="/login" />
        </Route>
        <Route path="/login" component={LoginPage} />
        <Route path="/developer" component={DeveloperView} />
        <Route exact path="/manager" component={ManagerView} />
        <Route exact path="/manager/task/:id" component={TaskDetailView} />
        <Route exact path="/manager/sprint/:id" component={SprintDetailView} />
      </Switch>
    </Router>
  );
}

export default App;
