import React, { useState, useEffect } from "react";
import { Tab, Tabs, Card, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from "@material-ui/core";
import { useHistory } from "react-router-dom";

function ManagerView() {
  const [activeTab, setActiveTab] = useState(0);
  const [subtasks, setSubtasks] = useState([]);
  const [developerStats, setDeveloperStats] = useState({});
  const [taskDetails, setTaskDetails] = useState(null);
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false); // Estado para el modal de detalles
  const [openCompletionDialog, setOpenCompletionDialog] = useState(false); // Estado para el modal de completar tarea
  const [selectedSubtask, setSelectedSubtask] = useState(null); // Subtarea seleccionada
  const [actualHours, setActualHours] = useState(0); // Estado para las horas reales
  const history = useHistory();

  useEffect(() => {
    const fetchSubtasks = async () => {
      const developerId = localStorage.getItem("developerId");
      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/developer/${developerId}`);
      const data = await response.json();
      setSubtasks(data);
    };

    const fetchDeveloperStats = async () => {
      const developerId = localStorage.getItem("developerId");
      const response = await fetch(`${process.env.REACT_APP_API_URL}developer-stats/${developerId}`);
      const data = await response.json();
      setDeveloperStats(data);
    };

    fetchSubtasks();
    fetchDeveloperStats();
  }, []);

  const handleViewDetails = async (subtaskId) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}/details`);
      const data = await response.json();
      setTaskDetails(data);
      setOpenDetailsDialog(true); // Abrir el modal de detalles
    } catch (error) {
      console.error("Error al obtener los detalles de la tarea:", error);
    }
  };

  // Función para abrir el modal de completar la subtarea
  const handleCompleteSubtask = (subtaskId) => {
    setSelectedSubtask(subtaskId);
    setOpenCompletionDialog(true); // Abrir el modal para completar la tarea
  };

  // Función para descompletar la subtarea
  const handleUncompleteSubtask = async (subtaskId) => {
    const subtask = subtasks.find((sub) => sub.id === subtaskId);

    const data = {
      title: subtask.title,
      completed: false,
      assignedDeveloperId: subtask.assignedDeveloperId,
      estimatedHours: subtask.estimatedHours,
      actualHours: 0, // Restaurar las horas reales a 0
      active: true,
    };

    const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (response.ok) {
      const updatedSubtasks = subtasks.map((subtask) =>
        subtask.id === subtaskId ? { ...subtask, completed: false, actualHours: 0 } : subtask
      );
      setSubtasks(updatedSubtasks);
    } else {
      console.error("Error al descompletar la subtarea");
    }
  };

  // Función para enviar la actualización con las horas reales
  const handleSubmitCompletion = async () => {
    if (selectedSubtask !== null) {
      const subtask = subtasks.find((sub) => sub.id === selectedSubtask);

      const data = {
        title: subtask.title,
        completed: true,
        assignedDeveloperId: subtask.assignedDeveloperId,
        estimatedHours: subtask.estimatedHours,
        actualHours: actualHours, // horas reales
        active: true,
      };

      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${selectedSubtask}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const updatedSubtasks = subtasks.map((subtask) =>
          subtask.id === selectedSubtask ? { ...subtask, completed: true, actualHours: actualHours } : subtask
        );
        setSubtasks(updatedSubtasks);
        setOpenCompletionDialog(false); // Cerrar el modal
      } else {
        console.error("Error al actualizar la subtarea");
      }
    }
  };

  return (
    <div>
      <h1>Bienvenido, {localStorage.getItem("name")}</h1>

      {/* Pestañas */}
      <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)} aria-label="developer tabs">
        <Tab label="Subtasks Asignadas" />
        <Tab label="Reporte Personal" />
      </Tabs>

      {/* Subtasks Asignadas */}
      {activeTab === 0 && (
        <div className="subtasks-container">
          {subtasks.map((subtask) => (
            <Card key={subtask.id} className="subtask-card">
              <h3>{subtask.title}</h3>
              <p>{subtask.completed ? "Completada" : "Pendiente"}</p>
              <Button onClick={() => handleViewDetails(subtask.id)}>Ver Detalles</Button>
              {!subtask.completed && (
                <Button onClick={() => handleCompleteSubtask(subtask.id)}>Marcar como Completada</Button>
              )}
              {subtask.completed && (
                <Button onClick={() => handleUncompleteSubtask(subtask.id)}>Descompletar</Button>
              )}
            </Card>
          ))}
        </div>
      )}

      {/* Reporte Personal */}
      {activeTab === 1 && (
        <div className="report-container">
          <h2>Reporte Personal</h2>
          <p>Tareas Asignadas: {developerStats.totalAssignedCount}</p>
          <p>Tareas Completadas: {developerStats.totalCompletedCount}</p>
          <p>Horas Estimadas: {developerStats.sumEstimatedHours}</p>
          <p>Horas Reales: {developerStats.sumActualHours}</p>
        </div>
      )}

      {/* Modal para ver detalles de la tarea */}
      <Dialog open={openDetailsDialog} onClose={() => setOpenDetailsDialog(false)}>
        <DialogTitle>Detalles de la Tarea</DialogTitle>
        <DialogContent>
          {taskDetails && (
            <>
              <h3>{taskDetails.title}</h3>
              <p>{taskDetails.task.description}</p>
              <p><strong>Sprint:</strong> {taskDetails.task.sprint ? `#${taskDetails.task.sprint.sprintNumber} (${taskDetails.task.sprint.startDate} - ${taskDetails.task.sprint.endDate})` : "No pertenece a ningún sprint"}</p>
              <p><strong>Horas Estimadas:</strong> {taskDetails.estimatedHours}</p>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDetailsDialog(false)} color="primary">
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal para ingresar horas reales y completar tarea */}
      <Dialog open={openCompletionDialog} onClose={() => setOpenCompletionDialog(false)}>
        <DialogTitle>Marcar Subtarea como Completada</DialogTitle>
        <DialogContent>
          <h3>Ingresar Horas Reales</h3>
          <TextField
            label="Horas Reales"
            type="number"
            value={actualHours}
            onChange={(e) => setActualHours(e.target.value)}
            fullWidth
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCompletionDialog(false)} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleSubmitCompletion} color="primary">
            Completar Tarea
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default ManagerView;
