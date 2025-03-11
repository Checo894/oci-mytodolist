import React, { useState } from "react";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import TextField from "@mui/material/TextField";
import DeleteIcon from "@mui/icons-material/Delete";

function SubtaskList({ task, updateTask }) {
  const [newSubtask, setNewSubtask] = useState("");

  // Agregar una subtarea
  const handleAddSubtask = async () => {
    if (!newSubtask.trim()) return;

    const newSubtaskData = {
      title: newSubtask,
      completed: false,
      assignedDeveloperId: 1, // Ajusta esto según tu lógica
    };

    try {
      const response = await fetch(`http://localhost:8080/subtasks/task/${task.id}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newSubtaskData),
      });

      if (!response.ok) throw new Error("Error al agregar la subtarea");

      const addedSubtask = await response.json();
      const updatedSubtasks = [...(task.subtasks || []), addedSubtask];

      updateTask({ ...task, subtasks: updatedSubtasks });
      setNewSubtask("");
    } catch (error) {
      console.error("Error al agregar subtarea:", error);
    }
  };

  // Marcar como completada o no completada
  const toggleSubtask = async (subtaskId) => {
    const subtask = task.subtasks.find((s) => s.id === subtaskId);
    const updatedSubtask = {
      title: subtask.title,
      completed: !subtask.completed,
    };

    try {
      await fetch(`http://localhost:8080/subtasks/${subtaskId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedSubtask),
      });

      const updatedSubtasks = task.subtasks.map((s) =>
        s.id === subtaskId ? { ...s, completed: !s.completed } : s
      );

      updateTask({ ...task, subtasks: updatedSubtasks });
    } catch (error) {
      console.error("Error al actualizar subtarea", error);
    }
  };

  // Eliminar una subtarea
  const deleteSubtask = async (subtaskId) => {
    try {
      const response = await fetch(`http://localhost:8080/subtasks/${subtaskId}`, {
        method: "DELETE",
      });

      if (!response.ok) throw new Error("Error al eliminar subtarea");

      const updatedSubtasks = task.subtasks.filter((s) => s.id !== subtaskId);
      updateTask({ ...task, subtasks: updatedSubtasks });
    } catch (error) {
      console.error("Error al eliminar subtarea", error);
    }
  };

  return (
    <div className="subtask-list">
      <h4>Subtasks</h4>
      <ul>
        {(task.subtasks ?? []).map((subtask) => (
          <li key={subtask.id}>
            <Checkbox checked={subtask.completed} onChange={() => toggleSubtask(subtask.id)} />
            {subtask.title}
            <Button
              variant="contained"
              color="secondary"
              size="small"
              startIcon={<DeleteIcon />}
              onClick={() => deleteSubtask(subtask.id)}
            >
              Delete
            </Button>
          </li>
        ))}
      </ul>

      <div>
        <TextField
          label="New Subtask"
          variant="outlined"
          size="small"
          value={newSubtask}
          onChange={(e) => setNewSubtask(e.target.value)}
        />
        <Button variant="contained" onClick={handleAddSubtask} size="small">
          +
        </Button>
      </div>
    </div>
  );
}

export default SubtaskList;
