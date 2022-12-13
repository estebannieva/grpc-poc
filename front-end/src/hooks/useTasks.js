import {useMath} from './useMath';
import {useEffect, useState} from 'react';
const {TaskServiceClient} = require('../proto/tasks_grpc_web_pb');
const {Category, CategoryId, Task, TaskId} = require('../proto/tasks_pb');

export const useTasks = (categoryId) => {

  const HOST = 'localhost';
  const PORT = 9091;
  const [tasks, setTasks] = useState([]);
  const [count, setCount] = useState(0);
  const [percent, setPercent] = useState(null);
  const [pct] = useMath();

  const error = (err) => {
    if (err === null || err === undefined) return;
    console.log('Error code: ' + err.code + ' "' + err.message + '"');
  }

  const listTasks = (categoryId, page = 0, size = 10) => {
    if (categoryId === null || categoryId === undefined) return;
    const client = new TaskServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new Category();
    req.setId(categoryId);
    req.setPage(page);
    req.setSize(size);

    const stream = client.listTasks(req);
    const data = [];
    stream.on('data', async (res) => {
      data.push({id: res.getId(), name: res.getName(), categoryId: res.getCategoryId()});
    });
    stream.on('error', error);
    stream.on('error', (err) => {
      setTasks((prev) => data);
      setCount((prev) => data.length);
    });
    stream.on('end', () => {
      setTasks((prev) => data);
      setCount((prev) => data.length);
    });
  }

  const createTask = (name) => {
    if (name === null || name === undefined || categoryId === null || categoryId === undefined) return;
    const client = new TaskServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new Task();
    req.setName(name);
    req.setCategoryId(categoryId);

    client.createTask(req, {}, (err, res) => {
      if (err) error(err);
      tasks.push({id: res.getId(), name: name, categoryId: categoryId});
      setCount((prev) => prev + 1);
    });
  }

  const deleteTask = (id) => {
    if (id === null || id === undefined) return;
    const client = new TaskServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new TaskId();
    req.setId(id);

    client.deleteTask(req, {}, (err, res) => {
      if (err) error(err);
      setCount((prev) => prev - 1);
    });
  }

  const countTasks = (categoryId) => {
    if (categoryId === null || categoryId === undefined) return;
    const client = new TaskServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new CategoryId();
    req.setId(categoryId);
    client.countTasks(req, {}, (err, res) => {
      if (err) {
        error(err);
      } else {
        setCount((prev) => res.getCount());
      }
    });
  }

  const downloadTasks = async () => {
    if (categoryId === null || categoryId === undefined) return;
    const opts = {
      suggestedName: 'tasks.csv',
      types: [
        {
          description: 'CSV file',
          accept: {
            'text/csv': ['.csv'],
          },
        },
      ]
    };
    const handle = await window.showSaveFilePicker(opts);
    const writable = await handle.createWritable();
    await writable.write('id,name,category id\r\n');

    const client = new TaskServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new Category();
    req.setId(categoryId);

    const stream = client.listTasks(req);
    let value = 0;
    stream.on('data', async (res) => {
      await writable.write(`${res.getId()},${res.getName().toLowerCase()},${res.getCategoryId()}\r\n`);
      value++;
      setPercent((prev) => pct(value, count));
    });
    stream.on('error', error);
    stream.on('end', () => {
      writable.close();
    });
  }

  useEffect(() => listTasks(categoryId, 0, 0), [categoryId]);

  return [tasks, count, percent, createTask, deleteTask, downloadTasks, countTasks];
}
