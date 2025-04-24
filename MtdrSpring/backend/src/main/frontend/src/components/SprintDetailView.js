import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { Button, Card, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Snackbar, SnackbarContent } from "@material-ui/core";

const SprintDetailView = () => {
    const { id } = useParams();
    const history = useHistory();
    const [sprint, setSprint] = useState(null);
    const [tasks, setTasks] = useState([]);
    const [openEditSprintDialog, setOpenEditSprintDialog] = useState(false);
    const [newSprintDetails, setNewSprintDetails] = useState({ sprintNumber: "", startDate: "", endDate: "" });
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarType, setSnackbarType] = useState("success");
    const [unassignedTasks, setUnassignedTasks] = useState([]);
    const [selectedTaskId, setSelectedTaskId] = useState("");
    const [openAddTaskDialog, setOpenAddTaskDialog] = useState(false);

    useEffect(() => {
      const fetchSprintData = async () => {
        const sprintResponse = await fetch(`${process.env.REACT_APP_API_URL}sprints/${id}`);
        const sprintData = await sprintResponse.json();
        setSprint(sprintData);
    
        const tasksResponse = await fetch(`${process.env.REACT_APP_API_URL}todolist/sprint/${id}`);
        if (tasksResponse.ok) {
          const tasksData = await tasksResponse.json();
          setTasks(tasksData); // Solo actualiza si se recibe una respuesta válida
        } else {
          setTasks([]); // Si no hay tareas, aseguramos que el estado sea un arreglo vacío
        }
      };

        const fetchUnassignedTasks = async () => {
            const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/without-sprint`); // Filtrar tareas donde sprint=null
            const data = await response.json();
            setUnassignedTasks(data);
            console.log("Tareas no asignadas:", data); 
        };
      
        fetchSprintData();
        fetchUnassignedTasks();
      }, [id]);
      
      const handleEditSprint = async () => {
        const data = {
          sprintNumber: newSprintDetails.sprintNumber,
          startDate: newSprintDetails.startDate,
          endDate: newSprintDetails.endDate,
        };
      
        const response = await fetch(`${process.env.REACT_APP_API_URL}sprints/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
        });
      
        if (response.ok) {
          const updatedSprint = await response.json();
          setSprint(updatedSprint);
          setSnackbarMessage("Sprint actualizado exitosamente.");
          setSnackbarType("success");
          setOpenEditSprintDialog(false); 
        } else {
          setSnackbarMessage("Error al actualizar el sprint.");
          setSnackbarType("error");
        }
        setOpenSnackbar(true);
      };

      const handleDeleteSprint = async () => {
        const response = await fetch(`${process.env.REACT_APP_API_URL}sprints/${id}`, {
          method: "DELETE",
        });
      
        if (response.ok) {
          setSnackbarMessage("Sprint eliminado exitosamente.");
          setSnackbarType("success");
          setTimeout(() => {
            history.push("/manager");
          }, 2000); // Redirige a la vista de administración de sprints
        } else {
          setSnackbarMessage("Error al eliminar el sprint.");
          setSnackbarType("error");
        }
        setOpenSnackbar(true);
      };

      const handleRemoveTaskFromSprint = async (taskId) => {
        // Buscamos la tarea original y mantenemos los campos intactos
        const taskToUpdate = tasks.find((task) => task.id === taskId);
      
        // Creamos el nuevo objeto con los campos no modificados
        const data = {
          ...taskToUpdate, // Mantiene todos los campos actuales
          sprint: null, // Solo eliminamos la relación con el sprint
        };
      
        const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${taskId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
        });
      
        if (response.ok) {
          const updatedTask = await response.json();
          setTasks(tasks.filter((task) => task.id !== updatedTask.id)); // Filtramos la tarea eliminada del sprint
          setSnackbarMessage("Tarea eliminada del sprint.");
          setSnackbarType("success");
        } else {
          setSnackbarMessage("Error al quitar tarea del sprint.");
          setSnackbarType("error");
        }
        setOpenSnackbar(true);
      };
      
      const handleAddTaskToSprint = async () => {
        const taskToAdd = unassignedTasks.find((task) => task.id === parseInt(selectedTaskId));
      
        if (taskToAdd) {
          const updatedTask = {
            ...taskToAdd, // Mantén todos los datos de la tarea original
            sprint: {       // Enviar el objeto completo del sprint
              id: id,       // Usamos el `id` del sprint actual
              sprintNumber: sprint.sprintNumber, // Agregar el número de sprint
              startDate: sprint.startDate,       // Fecha de inicio del sprint
              endDate: sprint.endDate,           // Fecha de fin del sprint
            },
          };
      
          console.log("Datos que se envían en el PUT:", updatedTask); // Verifica los datos
      
          const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${selectedTaskId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedTask),
          });
      
          if (response.ok) {
            const updated = await response.json();
            setTasks([...tasks, updated]); // Agregar la tarea al sprint en el listado de tareas
            setUnassignedTasks(unassignedTasks.filter((task) => task.id !== selectedTaskId)); // Eliminar de la lista de no asignadas
            setSnackbarMessage("Tarea asignada al sprint.");
            setSnackbarType("success");
            setOpenAddTaskDialog(false); // Cerrar el modal
          } else {
            console.error("Error al asignar la tarea. Respuesta:", response);
            setSnackbarMessage("Error al asignar la tarea.");
            setSnackbarType("error");
          }
          setOpenSnackbar(true);
        } else {
          console.error("Tarea no encontrada: ", selectedTaskId);
          setSnackbarMessage("Error: Tarea no encontrada.");
          setSnackbarType("error");
          setOpenSnackbar(true);
        }
      };
            
      

    return (
      <div className="sprint-detail-container">
        <div>

            <Button onClick={() => history.push("/manager")} color="primary">
                &lt; Regresar a la vista Principal
            </Button>

            <h2>Detalles del Sprint</h2>
            {sprint && (
                <>
                <p><strong>Sprint:</strong> {sprint.sprintNumber}</p>
                <p><strong>Fecha de inicio:</strong> {sprint.startDate}</p>
                <p><strong>Fecha de fin:</strong> {sprint.endDate}</p>
        
                <Button onClick={() => setOpenEditSprintDialog(true)}>Actualizar</Button>
                <Button onClick={handleDeleteSprint} color="secondary">Eliminar</Button>

                </>
            )}


            <h3>Tareas del Sprint #{sprint?.sprintNumber}</h3>

            <Button onClick={() => setOpenAddTaskDialog(true)} variant="contained" color="primary">
                Añadir Tarea al Sprint
            </Button>

            <div className="tasks-container">

            {tasks.map((task) => (
                <Card key={task.id} className="card-base">
                <h4>{task.title}</h4>
                <p>Status: {task.status}</p>
                <p>Progreso: {task.progress}%</p>
                <Button
                    onClick={() => handleRemoveTaskFromSprint(task.id)}
                    color="secondary"
                >
                    Quitar del Sprint
                </Button>
                </Card>
            ))}
            </div>

            <Dialog open={openEditSprintDialog} onClose={() => setOpenEditSprintDialog(false)}>
            <DialogTitle>Editar Sprint</DialogTitle>
            <DialogContent>
                <TextField
                label="Número de Sprint"
                value={newSprintDetails.sprintNumber}
                onChange={(e) => setNewSprintDetails({ ...newSprintDetails, sprintNumber: e.target.value })}
                fullWidth
                required
                />
                <TextField
                label="Fecha de Inicio"
                type="date"
                value={newSprintDetails.startDate}
                onChange={(e) => setNewSprintDetails({ ...newSprintDetails, startDate: e.target.value })}
                fullWidth
                required
                InputLabelProps={{ shrink: true }}
                />
                <TextField
                label="Fecha de Fin"
                type="date"
                value={newSprintDetails.endDate}
                onChange={(e) => setNewSprintDetails({ ...newSprintDetails, endDate: e.target.value })}
                fullWidth
                required
                InputLabelProps={{ shrink: true }}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpenEditSprintDialog(false)} color="primary">Cancelar</Button>
                <Button onClick={handleEditSprint} color="primary">Guardar</Button>
            </DialogActions>
            </Dialog>

            <Dialog open={openAddTaskDialog} onClose={() => setOpenAddTaskDialog(false)}>
            <DialogTitle>Agregar Tarea al Sprint</DialogTitle>
            <DialogContent>
            <TextField
              select
              label="Selecciona una tarea"
              value={selectedTaskId}
              onChange={(e) => {
                setSelectedTaskId(e.target.value); // Actualiza selectedTaskId correctamente
              }}
              fullWidth
              SelectProps={{ native: true }}
              required
            >
              <option value="">-- Selecciona una tarea --</option>
              {unassignedTasks.map((task) => (
                <option key={task.id} value={task.id}>
                  {task.title}
                </option>
              ))}
            </TextField>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpenAddTaskDialog(false)} color="primary">
                Cancelar
                </Button>
                <Button onClick={handleAddTaskToSprint} color="primary">
                Agregar
                </Button>
            </DialogActions>
            </Dialog>



            <Snackbar
            open={openSnackbar}
            autoHideDuration={3000}
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
        </div>

    );
};

export default SprintDetailView;