/*
## MyToDoReact version 1.0.
##
## Copyright (c) 2022 Oracle, Inc.
## Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl/
*/
/*
 * Component that supports creating a new todo item.
 * @author  jean.de.lavarene@oracle.com
 */

import React, { useState } from "react";
import Button from '@mui/material/Button';

function NewItem(props) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [endDate, setEndDate] = useState('');

  function handleSubmit(e) {
    e.preventDefault();
    if (!title.trim()) {
      return;
    }
    
    // Crear un nuevo objeto con los datos
    const newItem = {
      title: title,
      description: description,
      status: "Not Started",
      progress: 0,
      done: false,
      startDate: new Date().toISOString(),
      endDate: endDate ? new Date(endDate).toISOString() : null,
      subtasks: [],
    };
    
    // Llamar la función de agregar tarea del padre
    props.addItem(newItem);
    
    // Limpiar los campos
    setTitle("");
    setDescription("");
    setEndDate("");
  }

  return (
    <div id="newinputform">
      <form>
        <input
          id="titleInput"
          placeholder="Task Title"
          type="text"
          autoComplete="off"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <br />
        <textarea
          id="descriptionInput"
          placeholder="Description"
          rows="3"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <br />
        <input
          id="endDateInput"
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
        <br />
        <Button
          className="AddButton"
          variant="contained"
          disabled={props.isInserting}
          onClick={!props.isInserting ? handleSubmit : null}
          size="small"
        >
          {props.isInserting ? 'Adding…' : 'Add'}
        </Button>
      </form>
    </div>
  );
}

export default NewItem;
