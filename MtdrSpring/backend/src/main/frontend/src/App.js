import React from "react";
import { BrowserRouter as Router, Route, Switch, Redirect } from "react-router-dom";
import LoginPage from "./LoginPage";
import DeveloperView from "./DeveloperView.js"; // crea este después
import ManagerView from "./ManagerView";     // crea este también

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/">
          <Redirect to="/login" />
        </Route>
        <Route path="/login" component={LoginPage} />
        <Route path="/developer" component={DeveloperView} />
        <Route path="/manager" component={ManagerView} />
        {/* Puedes agregar un 404 aquí si quieres */}
      </Switch>
    </Router>
  );
}

export default App;
