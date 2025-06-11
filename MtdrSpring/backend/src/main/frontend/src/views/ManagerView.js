import React, { useState, useEffect } from "react";
import { Tab, Tabs, Card, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Snackbar, SnackbarContent } from "@material-ui/core";
import { useHistory } from "react-router-dom";
import {BarChart,Bar,PieChart,Pie,Cell,XAxis,YAxis,Tooltip,ResponsiveContainer,Legend,CartesianGrid} from "recharts";


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

    const [developers, setDevelopers] = useState([]);
    const [selectedDeveloper, setSelectedDeveloper] = useState("equipo");
    const [selectedSprint, setSelectedSprint] = useState(null);
    const [reportData, setReportData] = useState(null);
    
    const [completedSubtasks, setCompletedSubtasks] = useState([]);

    const [sprintStats, setSprintStats] = useState([]);
    const [devSprintStats, setDevSprintStats] = useState([]);



    const COLORS = [
      "#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#A28CFD", "#FF6699", "#82ca9d",
      "#8884d8", "#d0ed57", "#a4de6c", "#ffc658", "#FFB6C1", "#7FFFD4", "#BA55D3",
      "#FFD700", "#20B2AA", "#DC143C", "#4169E1", "#FF7F50", "#8A2BE2"
    ];
    


    const history = useHistory();

    // useEffect(() => {
    //     const fetchSubtasks = async () => {
    //         const developerId = localStorage.getItem("developerId");
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/developer/${developerId}`);
    //         const data = await response.json();
    //         setSubtasks(data);
    //     };
    
    //     const fetchDeveloperStats = async () => {
    //         const developerId = localStorage.getItem("developerId");
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}developer-stats/${developerId}`);
    //         const data = await response.json();
    //         setDeveloperStats(data);
    //     };

    //     const fetchTasks = async () => {
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}todolist`);
    //         const data = await response.json();
    //         setTasks(data);
    //     };

    //     const fetchSprints = async () => {
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}sprints`);
    //         const data = await response.json();
    //         setSprints(data);
    //     };

    //     const fetchDevelopers = async () => {
    //       const response = await fetch(`${process.env.REACT_APP_API_URL}developers`);
    //       const data = await response.json();
    //       setDevelopers(data);
    //     };

    //     const fetchSprintStats = async () => {
    //       try {
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}sprint-stats`);
    //         const data = await response.json();
    //         setSprintStats(data);
    //       } catch (error) {
    //         console.error("Error al obtener estadísticas de sprint:", error);
    //       }
    //     };

    //     const fetchDevSprintStats = async () => {
    //       try {
    //         const response = await fetch(`${process.env.REACT_APP_API_URL}developer-sprint-stats`);
    //         const data = await response.json();
    //         setDevSprintStats(data);
    //       } catch (error) {
    //         console.error("Error al obtener estadísticas de developer por sprint:", error);
    //       }
    //     };
  
    //     fetchSubtasks();
    //     fetchDevelopers();
    //     fetchDeveloperStats();
    //     fetchTasks();
    //     fetchSprints();
    //     fetchSprintStats();
    //     fetchDevSprintStats();
    // }, []);

    // useEffect(() => {
    //   const fetchReportData = async () => {
    //     if (!selectedSprint) return;
    
    //     try {
    //       let response;
    //       if (selectedDeveloper === "equipo") {
    //         response = await fetch(`${process.env.REACT_APP_API_URL}sprint-stats/${selectedSprint}`);
    //         const data = await response.json();
    //         setReportData({
    //           assigned: data.totalSubtasks,
    //           completed: data.totalCompleted,
    //           estimated: data.sumEstimatedHours,
    //           actual: data.sumActualHours,
    //           lastUpdatedTs: data.lastUpdatedTs,
    //         });
    //       } else {
    //         response = await fetch(`${process.env.REACT_APP_API_URL}developer-sprint-stats/developer/${selectedDeveloper}/sprint/${selectedSprint}`);
    //         const [data] = await response.json();
    //         if (data) {
    //           setReportData({
    //             assigned: data.totalAssignedCount,
    //             completed: data.totalCompletedCount,
    //             estimated: data.sumEstimatedHours,
    //             actual: data.sumActualHours,
    //             lastUpdatedTs: data.lastUpdatedTs,
    //           });
    //         } else {
    //           setReportData(null); // No hay datos
    //         }
    //       }
    //     } catch (error) {
    //       console.error("Error al obtener los datos del reporte:", error);
    //       setReportData(null);
    //     }  
    //   };
    //   const fetchCompletedSubtasks = async () => {
    //     try {
    //       const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/details`);
    //       const data = await response.json();
    //       // Filtrar por sprint seleccionado y solo completadas
    //       const filtered = data.filter(
    //         (subtask) =>
    //           subtask.completed === true &&
    //           subtask.task.sprint &&
    //           subtask.task.sprint.id === selectedSprint
    //       );
    //       setCompletedSubtasks(filtered);
    //     } catch (error) {
    //       console.error("Error al obtener subtareas completadas:", error);
    //     }
    //   };
    
    //   fetchReportData();
    //   fetchCompletedSubtasks();
    // }, [selectedDeveloper, selectedSprint]);
    


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

    const fetchDevelopers = async () => {
      const response = await fetch(`${process.env.REACT_APP_API_URL}developers`);
      const data = await response.json();
      setDevelopers(data);
    };

    const fetchSprintStats = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}sprint-stats`);
        const data = await response.json();
        setSprintStats(data);
      } catch (error) {
        console.error("Error al obtener estadísticas de sprint:", error);
      }
    };

    const fetchDevSprintStats = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}developer-sprint-stats`);
        const data = await response.json();
        setDevSprintStats(data);
      } catch (error) {
        console.error("Error al obtener estadísticas de developer por sprint:", error);
      }
    };

    useEffect(() => {
      const fetchReportData = async () => {
        if (!selectedSprint) return;
    
        try {
          let response;
          if (selectedDeveloper === "equipo") {
            response = await fetch(`${process.env.REACT_APP_API_URL}sprint-stats/${selectedSprint}`);
            const data = await response.json();
            setReportData({
              assigned: data.totalSubtasks,
              completed: data.totalCompleted,
              estimated: data.sumEstimatedHours,
              actual: data.sumActualHours,
              lastUpdatedTs: data.lastUpdatedTs,
            });
          } else {
            response = await fetch(`${process.env.REACT_APP_API_URL}developer-sprint-stats/developer/${selectedDeveloper}/sprint/${selectedSprint}`);
            const [data] = await response.json();
            if (data) {
              setReportData({
                assigned: data.totalAssignedCount,
                completed: data.totalCompletedCount,
                estimated: data.sumEstimatedHours,
                actual: data.sumActualHours,
                lastUpdatedTs: data.lastUpdatedTs,
              });
            } else {
              setReportData(null); // No hay datos
            }
          }
        } catch (error) {
          console.error("Error al obtener los datos del reporte:", error);
          setReportData(null);
        }  
      };
  
      const fetchCompletedSubtasks = async () => {
        try {
          const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/details`);
          const data = await response.json();
          // Filtrar por sprint seleccionado y solo completadas
          const filtered = data.filter(
            (subtask) =>
              subtask.completed === true &&
              subtask.task.sprint &&
              subtask.task.sprint.id === selectedSprint
          );
          setCompletedSubtasks(filtered);
        } catch (error) {
          console.error("Error al obtener subtareas completadas:", error);
        }
      };


      if (activeTab === 0) {
        fetchSubtasks();
        fetchDevelopers();
        fetchTasks();
        fetchSprints();
      } else if (activeTab === 1) {
        fetchDeveloperStats();
      } else if (activeTab === 2) {
        fetchTasks();
      } else if (activeTab === 3) {
        fetchSprints();
      } else if (activeTab === 4) {
        fetchReportData();
        fetchCompletedSubtasks();
      } else if (activeTab === 5) {
        fetchSprints();
        fetchSprintStats();
        fetchDevSprintStats();
      }
    }, [activeTab, selectedDeveloper, selectedSprint]);
    







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
            setSnackbarMessage("Tarea creada exitosamente.");
            setSnackbarType("success");
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

    const formattedDevSprintData = () => {
      const sprintMap = {};
    
      devSprintStats.forEach((entry) => {
        const sprintNumber = entry.sprint.sprintNumber;
        const sprintKey = `Sprint ${sprintNumber}`;
        const devName = entry.developer.name;
        const hours = entry.sumActualHours;
    
        if (!sprintMap[sprintNumber]) {
          sprintMap[sprintNumber] = { name: sprintKey, sprintNumber };
        }
    
        sprintMap[sprintNumber][devName] = hours;
      });
    
      // Ordenamos por sprintNumber (numérico) y luego removemos esa propiedad
      return Object.values(sprintMap)
        .sort((a, b) => a.sprintNumber - b.sprintNumber)
        .map(({ sprintNumber, ...rest }) => rest);
    };
    
    const formattedCompletedTasksData = () => {
      const sprintMap = {};
    
      devSprintStats.forEach((entry) => {
        const sprintNumber = entry.sprint.sprintNumber;
        const sprintKey = `Sprint ${sprintNumber}`;
        const devName = entry.developer.name;
        const completedTasks = entry.totalCompletedCount;
    
        if (!sprintMap[sprintNumber]) {
          sprintMap[sprintNumber] = { name: sprintKey, sprintNumber };
        }
    
        sprintMap[sprintNumber][devName] = completedTasks;
      });
    
      return Object.values(sprintMap)
        .sort((a, b) => a.sprintNumber - b.sprintNumber)
        .map(({ sprintNumber, ...rest }) => rest);
    };    

    const getTotalHoursAndCostPerDeveloper = () => {
      const developerTotals = {};
    
      devSprintStats.forEach((entry) => {
        const devName = entry.developer.name;
        const hours = entry.sumActualHours;
    
        if (!developerTotals[devName]) {
          developerTotals[devName] = 0;
        }
    
        developerTotals[devName] += hours;
      });
    
      return Object.entries(developerTotals).map(([devName, totalHours]) => ({
        developer: devName,
        totalHours: totalHours.toFixed(2),
        totalCost: `$${(totalHours * 25).toFixed(2)}`
      }));
    };

    const pendingSubtasks = subtasks.filter(sub => !sub.completed);
    const completedSubtasksSection = subtasks.filter(sub => sub.completed);

    

    return (
        <div>
        <h1 className="manager-header">Bienvenido, {localStorage.getItem("name")}</h1>

        {/* Pestañas */}
        <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)} aria-label="manager tabs">
            <Tab label="Subtasks Asignadas" />
            <Tab label="Reporte Personal" />
            <Tab label="Administración de Tareas" />
            <Tab label="Administración de Sprints" />
            <Tab label="Reportes" />
            <Tab label="Vista General" />
        </Tabs>

        {/* Subtasks Asignadas --------------------------------------------------------------------------------------------------------*/}
        {activeTab === 0 && (
            <div className="subtasks-container">
            <div className="subtasks-section">
            <h2>Subtareas Pendientes ({pendingSubtasks.length})</h2>
            <div className="subtasks-grid">
              {pendingSubtasks.length > 0 ? (
                pendingSubtasks.map((subtask) => (
                  <Card key={subtask.id} className="subtask-card">
                    <h3>{subtask.title}</h3>
                    <p>Pendiente</p>
                    <Button onClick={() => handleViewSubtaskDetails(subtask.id)}>Ver Detalles</Button>
                    <Button onClick={() => handleCompleteSubtask(subtask.id)}>Marcar como Completada</Button>
                  </Card>
                ))
              ) : (
                <p>No hay subtareas pendientes.</p>
              )}
            </div>
          </div>

          <div className="subtasks-section">
          <h2>Subtareas Completadas ({completedSubtasksSection.length})</h2>
            <div className="subtasks-grid">
              {completedSubtasksSection.length > 0 ? (
                completedSubtasksSection.map((subtask) => (
                  <Card key={subtask.id} className="subtask-card">
                    <h3>{subtask.title}</h3>
                    <p>Completada</p>
                    <Button onClick={() => handleViewSubtaskDetails(subtask.id)}>Ver Detalles</Button>
                    <Button onClick={() => handleUncompleteSubtask(subtask.id)}>Descompletar</Button>
                  </Card>
                ))
              ) : (
                <p>No hay subtareas completadas.</p>
              )}
            </div>
          </div>
            </div>
        )}

        {/* Modal para ver detalles de la tarea en subtarea*/}
        <Dialog open={subtaskOpenDetailsDialog} onClose={() => setSubtaskOpenDetailsDialog(false)}>
            <DialogTitle>Detalles de la Tarea</DialogTitle>
            <DialogContent>
            {subtaskTaskDetails && (
                <>
                <h3>{subtaskTaskDetails.task.title}</h3>
                <p>{subtaskTaskDetails.task.description}</p>
                <p><strong>Estado:</strong> {subtaskTaskDetails.task.status}</p>
                <p><strong>Progreso:</strong> {subtaskTaskDetails.task.progress}</p>
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
                <Card key={task.id} className="card-base">
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
              <Card key={sprint.id} className="card-base">
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
            style={{ marginTop: "1rem" }}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            label="Fecha de Fin"
            type="date"
            value={newSprint.endDate}
            onChange={(e) => setNewSprint({ ...newSprint, endDate: e.target.value })}
            fullWidth
            required
            style={{ marginTop: "1rem" }}
            InputLabelProps={{ shrink: true }}
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



    
      {/* Reportes ---------------------------------------------------------------------------------------------------------------*/}
      {activeTab === 4 && (
      <div className="report-filter">
      <h2>Reportes por Sprint</h2>

      <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
        <TextField
          select
          label="Seleccionar desarrollador"
          value={selectedDeveloper}
          onChange={(e) => setSelectedDeveloper(e.target.value)}
          SelectProps={{ native: true }}
          variant="outlined"
        >
          <option value="equipo">Equipo completo</option>
          {developers.map((dev) => (
            <option key={dev.id} value={dev.id}>{dev.name}</option>
          ))}
        </TextField>

        <TextField
          select
          label="Seleccionar sprint"
          value={selectedSprint || ""}
          onChange={(e) => setSelectedSprint(Number(e.target.value))}
          SelectProps={{ native: true }}
          variant="outlined"
        >
          {sprints.map((sprint) => (
            <option key={sprint.id} value={sprint.id}>
              Sprint #{sprint.sprintNumber}
            </option>
          ))}
        </TextField>
      </div>

      {reportData ? (
          <div style={{ marginTop: "2rem" }}>
            <p><strong>Last Updated: </strong>{new Date(reportData.lastUpdatedTs).toLocaleString()}</p>
            <h3>Resumen de Actividad</h3>
            <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap" }}>
              <Card style={{ padding: "1rem", minWidth: "200px" }}>
                <h4>Horas Estimadas</h4>
                <p>{reportData.estimated} hrs</p>
              </Card>
              <Card style={{ padding: "1rem", minWidth: "200px" }}>
                <h4>Horas Reales</h4>
                <p>{reportData.actual} hrs</p>
              </Card>
              <Card style={{ padding: "1rem", minWidth: "200px" }}>
                <h4>Subtareas Asignadas</h4>
                <p>{reportData.assigned}</p>
              </Card>
              <Card style={{ padding: "1rem", minWidth: "200px" }}>
                <h4>Subtareas Completadas</h4>
                <p>{reportData.completed}</p>
              </Card>
            </div>
          </div>
        ) : (
          <p style={{ marginTop: "1rem" }}>No hay datos disponibles para esta combinación.</p>
        )}

      {reportData && (
        <div style={{ marginTop: "2rem" }}>
          <h3>Comparación de Horas</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={[{ name: "Horas", Estimadas: reportData.estimated, Reales: reportData.actual }]}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="Estimadas" fill="#8884d8" />
              <Bar dataKey="Reales" fill="#82ca9d" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}
      {reportData && (
        <div style={{ marginTop: "2rem" }}>
          <h3>Progreso de Subtareas</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={[
                  { name: "Completadas", value: reportData.completed },
                  { name: "Pendientes", value: reportData.assigned - reportData.completed },
                ]}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={100}
                fill="#8884d8"
                label
              >
                <Cell fill="#00C49F" />
                <Cell fill="#FF8042" />
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}

      {completedSubtasks.length > 0 && (
        <div style={{ marginTop: "3rem" }}>
          <h3>Subtareas Completadas del Sprint</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Subtarea</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Desarrollador</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Tarea</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Inicio Sprint</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Fin Sprint</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Horas Estimadas</th>
                <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Horas Reales</th>
              </tr>
            </thead>
            <tbody>
              {completedSubtasks.map((sub) => (
                <tr key={sub.id}>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.title}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>
                    {developers.find(dev => dev.id === sub.assignedDeveloperId)?.name || "N/A"}
                  </td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.task.title}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.task.sprint.startDate}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.task.sprint.endDate}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.estimatedHours}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{sub.actualHours ?? "N/A"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}


    </div>
     )}

      {/* Vista General ----------------------------------------------------------------------------------------------------------*/}
      {activeTab === 5 && (
      <div className="general-view">
        {/* <h2>Vista General del Proyecto</h2>
        <p>Esta es la vista general de las estadísticas del proyecto y sus desarrolladores.</p> */}

        {/* Gráfica de Horas Invertidas por Sprint */}
        <div style={{ marginTop: "2rem" }}>
          <h3>Horas Invertidas por Sprint</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={sprintStats.map((sprint) => ({
                name: `Sprint ${sprint.sprintId}`,
                horas: sprint.sumActualHours,
              }))}
              margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis label={{ value: 'Horas', angle: -90, position: 'insideLeft' }} />
              <Tooltip />
              <Legend />
              <Bar dataKey="horas" fill="#c084fc" name="Hours Invested" />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div style={{ marginTop: "4rem" }}>
        <h3>Horas Trabajadas por Developer por Sprint</h3>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={formattedDevSprintData()}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis label={{ value: "Horas trabajadas", angle: -90, position: "insideLeft" }} />
            <Tooltip />
            <Legend />
            {Array.from(
              new Set(devSprintStats.map((entry) => entry.developer.name))
            ).map((devName, index) => (
              <Bar key={devName} dataKey={devName} fill={COLORS[index % COLORS.length]} />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </div>
      <div style={{ marginTop: "4rem" }}>
        <h3>Tareas Completadas por Developer por Sprint</h3>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={formattedCompletedTasksData()}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis label={{ value: "Tareas completadas", angle: -90, position: "insideLeft" }} />
            <Tooltip />
            <Legend />
            {Array.from(new Set(devSprintStats.map((entry) => entry.developer.name)))
              .map((devName, index) => (
                <Bar key={devName} dataKey={devName} fill={COLORS[index % COLORS.length]} />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </div>
      <div style={{ marginTop: "4rem" }}>
        <h3>Horas Totales y Costo por Developer</h3>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Developer</th>
              <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Horas Totales</th>
              <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>Costo Total (USD)</th>
            </tr>
          </thead>
          <tbody>
            {getTotalHoursAndCostPerDeveloper().map((row) => (
              <tr key={row.developer}>
                <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{row.developer}</td>
                <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{row.totalHours}</td>
                <td style={{ borderBottom: "1px solid #eee", padding: "8px" }}>{row.totalCost}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>


      </div>
      
    )}


      {/* Botón para cerrar sesión */}


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
