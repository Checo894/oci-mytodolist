import React, { useState } from "react";
import Button from "@mui/material/Button";
import { Logout } from "@mui/icons-material";
import SprintSection from "./SprintSection";
import TaskSection from "./TaskSection";


function ManagerView() {
  const [view, setView] = useState("sprints");
  const name = localStorage.getItem("name");

  const renderContent = () => {
    switch (view) {
      case "sprints":
        return <SprintSection />;
      case "tasks":
        return <TaskSection />;        
      case "all":
        return <div>📊 Aquí irá la vista de All Time</div>;
      default:
        return null;
    }
  };

  return (
    <div style={{ display: "flex", height: "100vh", fontFamily: "sans-serif" }}>
      {/* Sidebar */}
      <div style={{
        width: "200px",
        background: "#f0f5f1",
        padding: "1rem",
        display: "flex",
        flexDirection: "column",
        gap: "1rem"
      }}>
        <Button
          variant={view === "sprints" ? "contained" : "outlined"}
          style={{ backgroundColor: view === "sprints" ? "#A7D7C5" : "#DFF5E1" }}
          onClick={() => setView("sprints")}
        >
          Sprints
        </Button>
        <Button
          variant={view === "tasks" ? "contained" : "outlined"}
          style={{ backgroundColor: view === "tasks" ? "#A7D7C5" : "#DFF5E1" }}
          onClick={() => setView("tasks")}
        >
          Tasks
        </Button>
        <Button
          variant={view === "all" ? "contained" : "outlined"}
          style={{ backgroundColor: view === "all" ? "#A7D7C5" : "#DFF5E1" }}
          onClick={() => setView("all")}
        >
          All Time
        </Button>
      </div>

      {/* Main Section */}
      <div style={{ flex: 1, background: "#fdfdfd", padding: "1rem" }}>
        {/* Top bar */}
        <div style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "1rem"
        }}>
          <div>
            <h3>Hello, {name}</h3>
            <span style={{
              background: "#4CAF50",
              color: "white",
              padding: "4px 8px",
              borderRadius: "8px",
              fontSize: "0.8rem"
            }}>
              Project Manager
            </span>
          </div>
          <Button
            variant="outlined"
            color="error"
            startIcon={<Logout />}
            onClick={() => {
              localStorage.clear();
              window.location.href = "/login";
            }}
          >
            Logout
          </Button>
        </div>

        {/* Dynamic content based on view */}
        {renderContent()}
      </div>
    </div>
  );
}

export default ManagerView;
