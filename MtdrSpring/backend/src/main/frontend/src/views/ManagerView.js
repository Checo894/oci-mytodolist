import React, { useState, useEffect } from "react";
import { Tab, Tabs, Card, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Snackbar, SnackbarContent } from "@material-ui/core";
import { useHistory } from "react-router-dom";

function ManagerView() {
    const [activeTab, setActiveTab] = useState(0);
    const [openSnackbar, setOpenSnackbar] = useState(false); // Estado para el Snackbar
    const [snackbarMessage, setSnackbarMessage] = useState(""); // Mensaje del Snackbar
    const [snackbarType, setSnackbarType] = useState("success");

    const [subtasks, setSubtasks] = useState([]);
    const [developerStats, setDeveloperStats] = useState({});
    const [subtaskTaskDetails, setSubtaskTaskDetails] = useState(null);
    const [subtaskOpenDetailsDialog, setSubtaskOpenDetailsDialog] = useState(false); // Estado para el modal de detalles en subtarea
    const [openCompletionDialog, setOpenCompletionDialog] = useState(false); // Estado para el modal de completar subtarea
    const [selectedSubtask, setSelectedSubtask] = useState(null); // Subtarea seleccionada
    const [actualHours, setActualHours] = useState(0); // Estado para las horas reales

    const [tasks, setTasks] = useState([]);
    const [newTask, setNewTask] = useState({ title: "", description: "" });
    const [taskOpenCreateDialog, setTaskOpenCreateDialog] = useState(false); // Modal para crear tarea
    // const [taskDetails, setTaskDetails] = useState(null);
    // const [openDetailsDialog, setOpenDetailsDialog] = useState(false); // Modal de detalles
    // const [openUpdateDialog, setOpenUpdateDialog] = useState(false); // Modal para actualizar tarea
    // const [selectedTaskId, setSelectedTaskId] = useState(null); // Tarea seleccionada

    const [sprints, setSprints] = useState([]); // Estado para los sprints
    const [sprintOpenCreateDialog, setSprintOpenCreateDialog] = useState(false); // Modal para crear sprint
    const [newSprint, setNewSprint] = useState({ sprintNumber: "", startDate: "", endDate: "" }); // Estado para el nuevo sprint

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

        const fetchTasks = async () => {
            const response = await fetch(`${process.env.REACT_APP_API_URL}todolist`);
            const data = await response.json();
            setTasks(data);
        };

        const fetchSprints = async () => {
            const response = await fetch(`${process.env.REACT_APP_API_URL}sprints`);
            const data = await response.json();
            setSprints(data);
        };
  
        fetchSubtasks();
        fetchDeveloperStats();
        fetchTasks();
        fetchSprints();
    }, []);

    const handleViewSubtaskDetails = async (subtaskId) => {
        try {
          const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}/details`);
          const data = await response.json();
          setSubtaskTaskDetails(data);
          setSubtaskOpenDetailsDialog(true); // Abrir el modal de detalles
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
          setSnackbarMessage("Subtarea descompletada exitosamente.");
          setSnackbarType("success");
        } else {
          console.error("Error al descompletar la subtarea");
          setSnackbarMessage("Error al descompletar la subtarea.");
          setSnackbarType("error");
        }
        setOpenSnackbar(true);
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
            setSnackbarMessage("Subtarea completada exitosamente.");
            setSnackbarType("success");
          } else {
            console.error("Error al actualizar la subtarea");
            setSnackbarMessage("Error al actualizar la subtarea.");
            setSnackbarType("error");
          }
        }
        setOpenSnackbar(true);
      };

    // Abrir el modal para ver los detalles de una tarea
    // const handleViewDetails = async (taskId) => {
    //     const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${taskId}`);
    //     const data = await response.json();
    //     setTaskDetails(data);
    //     setOpenDetailsDialog(true);
    // };

    // Abrir el modal para crear una nueva tarea
    const handleCreateTask = () => {
        setTaskOpenCreateDialog(true);
    };

    // Enviar la solicitud para crear una nueva tarea
    const handleSubmitCreateTask = async () => {
        const data = {
            title: newTask.title,
            description: newTask.description,
            status: "Not Started",
            sprint: null,
        };
    
        const response = await fetch(`${process.env.REACT_APP_API_URL}todolist`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });
    
        if (response.ok) {
            const data = await response.json();
            setTasks([...tasks, data]);
            setTaskOpenCreateDialog(false);
            setNewTask({ title: "", description: "" }); // Reset form
        } else {
            console.error("Error al crear la tarea");
            setSnackbarMessage("Error al crear la tarea.");
            setSnackbarType("error");
        }
        setOpenSnackbar(true);
    };
      
    // Abrir el modal para actualizar una tarea
    // const handleUpdateTask = (taskId) => {
    //     const taskToUpdate = tasks.find((task) => task.id === taskId);
    //     setSelectedTaskId(taskId);
    //     setNewTask({ ...taskToUpdate });
    //     setOpenUpdateDialog(true);
    // };

    // Enviar la solicitud para actualizar una tarea
    // const handleSubmitUpdateTask = async () => {
    //     const currentTask = tasks.find((task) => task.id === selectedTaskId);
      
    //     const data = {
    //       title: newTask.title,
    //       description: newTask.description,
    //       status: currentTask.status,
    //       sprint: currentTask.sprint,
    //     };
      
    //     const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${selectedTaskId}`, {
    //       method: "PUT",
    //       headers: { "Content-Type": "application/json" },
    //       body: JSON.stringify(data),
    //     });
      
    //     if (response.ok) {
    //       const updatedTask = await response.json();
    //       const updatedTasks = tasks.map((task) =>
    //         task.id === selectedTaskId ? updatedTask : task
    //       );
    //       setTasks(updatedTasks);
    //       setOpenUpdateDialog(false);
    //     }
    //   };      

    // Eliminar una tarea
    // const handleDeleteTask = async (taskId) => {
    //     const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${taskId}`, {
    //         method: "DELETE",
    //     });

    //     if (response.ok) {
    //         const filteredTasks = tasks.filter((task) => task.id !== taskId);
    //         setTasks(filteredTasks);
    //     }
    // };

    // Abrir el modal para crear un nuevo sprint
    const handleCreateSprint = () => {
        setSprintOpenCreateDialog(true);
    };

    // Enviar la solicitud para crear un nuevo sprint
    const handleSubmitCreateSprint = async () => {
        const data = {
            sprintNumber: newSprint.sprintNumber,
            startDate: newSprint.startDate,
            endDate: newSprint.endDate,
        };
    
        const response = await fetch(`${process.env.REACT_APP_API_URL}sprints`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });
    
        if (response.ok) {
            const data = await response.json();
            setSprints([...sprints, data]);
            setSprintOpenCreateDialog(false);
            setNewSprint({ sprintNumber: "", startDate: "", endDate: "" }); // Reset form
            setSnackbarMessage("Sprint creado exitosamente.");
            setSnackbarType("success");
        } else {
            console.error("Error al crear el sprint");
            setSnackbarMessage("Error al crear el sprint.");
            setSnackbarType("error");
        }
        setOpenSnackbar(true);
    };

    return (
        <div>
        <h1>Bienvenido, {localStorage.getItem("name")}</h1>

        {/* Pestañas */}
        <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)} aria-label="manager tabs">
            <Tab label="Subtasks Asignadas" />
            <Tab label="Reporte Personal" />
            <Tab label="Administración de Tareas" />
            <Tab label="Administración de Sprints" />
            <Tab label="Reportes" />
        </Tabs>

        {/* Subtasks Asignadas --------------------------------------------------------------------------------------------------------*/}
        {activeTab === 0 && (
            <div className="subtasks-container">
            {subtasks.map((subtask) => (
                <Card key={subtask.id} className="subtask-card">
                <h3>{subtask.title}</h3>
                <p>{subtask.completed ? "Completada" : "Pendiente"}</p>
                <Button onClick={() => handleViewSubtaskDetails(subtask.id)}>Ver Detalles</Button>
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

        {/* Modal para ver detalles de la tarea en subtarea*/}
        <Dialog open={subtaskOpenDetailsDialog} onClose={() => setSubtaskOpenDetailsDialog(false)}>
            <DialogTitle>Detalles de la Tarea</DialogTitle>
            <DialogContent>
            {subtaskTaskDetails && (
                <>
                <h3>{subtaskTaskDetails.title}</h3>
                <p>{subtaskTaskDetails.task.description}</p>
                <p><strong>Estado:</strong> {subtaskTaskDetails.status}</p>
                <p><strong>Progreso:</strong> {subtaskTaskDetails.progress}</p>
                <p><strong>Sprint:</strong> {subtaskTaskDetails.task.sprint ? `#${subtaskTaskDetails.task.sprint.sprintNumber} (${subtaskTaskDetails.task.sprint.startDate} - ${subtaskTaskDetails.task.sprint.endDate})` : "No pertenece a ningún sprint"}</p>
                <p><strong>Horas Estimadas:</strong> {subtaskTaskDetails.estimatedHours}</p>
                <p><strong>Horas Reales:</strong> {subtaskTaskDetails.completed ? subtaskTaskDetails.actualHours : "N/A"}</p>
                </>
            )}
            </DialogContent>
            <DialogActions>
            <Button onClick={() => setSubtaskOpenDetailsDialog(false)} color="primary">
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


    
        {/* Reporte Personal ----------------------------------------------------------------------------------------------------------*/}
        {activeTab === 1 && (
            <div className="report-container">
            <h2>Reporte Personal</h2>
            <p>Tareas Asignadas: {developerStats.totalAssignedCount}</p>
            <p>Tareas Completadas: {developerStats.totalCompletedCount}</p>
            <p>Horas Estimadas: {developerStats.sumEstimatedHours}</p>
            <p>Horas Reales: {developerStats.sumActualHours}</p>
            </div>
        )}
    


        {/* Pestaña de administración de tareas ---------------------------------------------------------------------------------------*/}
        {activeTab === 2 && (
            <div className="task-container">
            <Button onClick={handleCreateTask} variant="contained" color="primary">
                Añadir Tarea
            </Button>

            {/* Lista de tareas */}
            <div className="tasks-list">
                {tasks.map((task) => (
                <Card key={task.id} className="task-card">
                    <h3>{task.title}</h3>
                    <p>Status: {task.status}</p>
                    <p>Progreso: {task.progress}%</p>
                    <Button onClick={() => history.push(`/manager/task/${task.id}`)}>Ver Detalles</Button>
                    {/* <Button onClick={() => handleUpdateTask(task.id)}>Actualizar</Button>
                    <Button onClick={() => handleDeleteTask(task.id)}>Eliminar</Button> */}
                </Card>
                ))}
            </div>
            </div>
        )}

      {/* Modal para ver los detalles de la tarea */}
      {/* <Dialog open={openDetailsDialog} onClose={() => setOpenDetailsDialog(false)}>
        <DialogTitle>Detalles de la Tarea</DialogTitle>
        <DialogContent>
          {taskDetails && (
            <>
              <h3>{taskDetails.title}</h3>
              <p>{taskDetails.description}</p>
              <p>Status: {taskDetails.status}</p>
              <p>Progreso: {taskDetails.progress}%</p>
              <p>Fecha de inicio: {taskDetails.startDate}</p>
              <p>Fecha de fin: {taskDetails.endDate}</p>
              <p>Sprint: {taskDetails.sprint ? `Sprint #${taskDetails.sprint.sprintNumber}` : "No asignado"}</p>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDetailsDialog(false)} color="primary">
            Cerrar
          </Button>
        </DialogActions>
      </Dialog> */}

      {/* Modal para crear una nueva tarea */}
      <Dialog open={taskOpenCreateDialog} onClose={() => setTaskOpenCreateDialog(false)}>
        <DialogTitle>Añadir Nueva Tarea</DialogTitle>
        <DialogContent>
          <TextField
            label="Título"
            value={newTask.title}
            onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Descripción"
            value={newTask.description}
            onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
            fullWidth
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTaskOpenCreateDialog(false)} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleSubmitCreateTask} color="primary">
            Crear Tarea
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal para actualizar una tarea */}
      {/* <Dialog open={openUpdateDialog} onClose={() => setOpenUpdateDialog(false)}>
        <DialogTitle>Actualizar Tarea</DialogTitle>
        <DialogContent>
          <TextField
            label="Título"
            value={newTask.title}
            onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Descripción"
            value={newTask.description}
            onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
            fullWidth
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenUpdateDialog(false)} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleSubmitUpdateTask} color="primary">
            Actualizar Tarea
          </Button>
        </DialogActions>
      </Dialog> */}



      {/* Pestaña de administración de sprints ---------------------------------------------------------------------------------------*/}
      {activeTab === 3 && (
        <div className="sprint-container">
          <Button onClick={handleCreateSprint} variant="contained" color="primary">
            Añadir Sprint
          </Button>

          {/* Lista de sprints */}
          <div className="sprints-list">
            {sprints.map((sprint) => (
              <Card key={sprint.id} className="sprint-card">
                <h3>Sprint #{sprint.sprintNumber}</h3>
                <p>Fecha de Inicio: {sprint.startDate}</p>
                <p>Fecha de Fin: {sprint.endDate}</p>
                <Button onClick={() => history.push(`/manager/sprint/${sprint.id}`)}>Ver Detalles</Button>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* Modal para crear un nuevo sprint */}
      <Dialog open={sprintOpenCreateDialog} onClose={() => setSprintOpenCreateDialog(false)}>
        <DialogTitle>Añadir Nuevo Sprint</DialogTitle>
        <DialogContent>
          <TextField
            label="Número de Sprint"
            value={newSprint.sprintNumber}
            onChange={(e) => setNewSprint({ ...newSprint, sprintNumber: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Fecha de Inicio"
            type="date"
            value={newSprint.startDate}
            onChange={(e) => setNewSprint({ ...newSprint, startDate: e.target.value })}
            fullWidth
            required
          />
          <TextField
            label="Fecha de Fin"
            type="date"
            value={newSprint.endDate}
            onChange={(e) => setNewSprint({ ...newSprint, endDate: e.target.value })}
            fullWidth
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSprintOpenCreateDialog(false)} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleSubmitCreateSprint} color="primary">
            Crear Sprint
          </Button>
        </DialogActions>
      </Dialog>




      {/* Snackbar para mostrar mensajes */}
      <Snackbar
          open={openSnackbar}
          autoHideDuration={3000} // Duración en milisegundos
          onClose={() => setOpenSnackbar(false)}
          >
          <SnackbarContent
              style={{
              backgroundColor: snackbarType === "success" ? "green" : "red",
              }}
              message={snackbarMessage}
          />
      </Snackbar>
    </div>
  );
}

export default ManagerView;
