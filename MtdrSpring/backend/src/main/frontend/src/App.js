import React, { useState, useEffect } from "react";
import NewItem from "./NewItem";
import SubtaskList from "./SubtaskList";
import API_LIST from "./API";
import DeleteIcon from "@mui/icons-material/Delete";
import { Button, CircularProgress, LinearProgress } from "@mui/material";
import Moment from "react-moment";

function App() {
  const [isLoading, setLoading] = useState(false);
  const [isInserting, setInserting] = useState(false);
  const [items, setItems] = useState([]);
  const [error, setError] = useState();
  const [expandedTask, setExpandedTask] = useState(null);

  useEffect(() => {
    setLoading(true);
    fetch(API_LIST)
      .then((response) => response.json())
      .then(async (tasks) => {
        const tasksWithSubtasks = await Promise.all(
          tasks.map(async (task) => {
            try {
              const subtaskResponse = await fetch(`http://localhost:8080/subtasks/task/${task.id}`);
              const subtasks = subtaskResponse.ok ? await subtaskResponse.json() : [];
              const progress = calculateProgress(subtasks);
              const status = determineStatus(progress, task.endDate); 
              return { ...task, subtasks, progress, status };
            } catch (error) {
              console.error(`Error fetching subtasks for task ${task.id}:`, error);
              return { ...task, subtasks: [], progress: 0, status: "Not Started" };
            }
          })
        );
  
        setLoading(false);
        setItems(tasksWithSubtasks);
      })
      .catch((error) => {
        setLoading(false);
        setError(error);
      });
  }, []);
  

  function calculateProgress(subtasks) {
    if (subtasks.length === 0) return 0;
    const completedCount = subtasks.filter((sub) => sub.completed).length;
    return Math.round((completedCount / subtasks.length) * 100);
  }

  function determineStatus(progress, endDate) {
    const now = new Date();
    if (endDate && new Date(endDate) < now && progress < 100) return "Incomplete";
    if (progress === 100) return "Completed";
    if (progress === 0) return "Not Started";
    if (progress > 0 && progress < 100) return "In Progress";
    return "In Progress";
  }

  function toggleExpand(id) {
    setExpandedTask(expandedTask === id ? null : id);
  }

  function deleteItem(deleteId) {
    fetch(`${API_LIST}/${deleteId}`, { method: "DELETE" })
      .then((response) => response.ok && setItems(items.filter((item) => item.id !== deleteId)))
      .catch(setError);
  }

  function addItem(newTask) {
    setInserting(true);
    fetch(API_LIST, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newTask),
    })
      .then((response) => (response.ok ? response.json() : Promise.reject("Something went wrong ...")))
      .then((task) => setItems((prevItems) => [task, ...prevItems]))
      .catch(setError)
      .finally(() => setInserting(false));
  }

  function updateTask(updatedTask) {
    const updatedProgress = calculateProgress(updatedTask.subtasks);
    const updatedStatus = determineStatus(updatedProgress, updatedTask.endDate);
    setItems(items.map((item) => (item.id === updatedTask.id ? { ...updatedTask, progress: updatedProgress, status: updatedStatus } : item)));
  }

  return (
    <div className="App">
      <h1>MY TODO LIST</h1>
      <NewItem addItem={addItem} isInserting={isInserting} />
      {error && <p>Error: {error.message}</p>}
      {isLoading && <CircularProgress />}
      {!isLoading && (
        <div id="maincontent">
          {items.map((task) => (
            <div key={task.id} className="task-box">
              <div className="task-header" onClick={() => toggleExpand(task.id)}>
                <h3>{task.title}</h3>
                <Moment format="MMM Do hh:mm:ss">{task.creation_ts}</Moment>
                <Button variant="contained" startIcon={<DeleteIcon />} onClick={() => deleteItem(task.id)} size="small">
                  DELETE
                </Button>
              </div>

              {/* Status de la tarea */}
              <p className="task-status">
                <strong>Status:</strong> <span className={`status ${task.status.toLowerCase().replace(" ", "-")}`}>{task.status}</span>
              </p>

              {/* Barra de progreso */}
              <div className="progress-container">
                <LinearProgress
                  variant="determinate"
                  value={task.progress}
                  style={{
                    height: "8px",
                    borderRadius: "5px",
                    backgroundColor: "#ddd",
                  }}
                  sx={{
                    "& .MuiLinearProgress-bar": {
                      backgroundColor: task.progress < 30 ? "red" : task.progress < 70 ? "yellow" : "green",
                    },
                  }}
                />
                <p className="progress-label">{task.progress}%</p>
              </div>

              {expandedTask === task.id && (
                <div className="task-details">
                  <p><strong>Description:</strong> {task.description}</p>
                  <p><strong>End Date:</strong> {task.endDate || "No deadline"}</p>
                  <SubtaskList task={task} updateTask={updateTask} />
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default App;
