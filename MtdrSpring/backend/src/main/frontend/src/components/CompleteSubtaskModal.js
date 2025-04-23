// src/components/CompleteSubtaskModal.js
import React, { useState } from "react";
import { Modal, Button, TextField } from "@material-ui/core";

function CompleteSubtaskModal({ open, onClose, onSubmit, subtask }) {
  const [actualHours, setActualHours] = useState("");

  const handleSubmit = () => {
    if (isNaN(actualHours) || actualHours <= 0) {
      alert("Por favor ingresa un valor válido para las horas.");
      return;
    }

    // Enviar la actualización de la subtarea
    onSubmit(subtask.id, actualHours);
    onClose(); // Cerrar el modal después de enviar
  };

  return (
    <Modal open={open} onClose={onClose}>
      <div className="modal-container">
        <h2>Marcar como completada</h2>
        <p>Tarea: {subtask.title}</p>
        <TextField
          label="Horas reales"
          type="number"
          value={actualHours}
          onChange={(e) => setActualHours(e.target.value)}
          required
        />
        <Button onClick={handleSubmit}>Marcar como completada</Button>
        <Button onClick={onClose}>Cancelar</Button>
      </div>
    </Modal>
  );
}

export default CompleteSubtaskModal;
