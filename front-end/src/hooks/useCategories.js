import {useEffect, useState} from 'react';
const {CategoryServiceClient} = require('../proto/categories_grpc_web_pb');
var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js');

export const useCategories = () => {

  const HOST = 'localhost';
  const PORT = 9090;
  const [categories, setCategories] = useState([]);

  const error = (err) => {
    if (err == null || err === undefined) return;
    console.log('Error code: ' + err.code + ' "' + err.message + '"');
  }

  const listCategories = () => {
    const client = new CategoryServiceClient(`http://${HOST}:${PORT}`, null, null);

    const req = new google_protobuf_empty_pb.Empty();

    const stream = client.listCategories(req);
    const data = [];
    stream.on('data', async (res) => {
      data.push({id: res.getId(), name: res.getName(), count: res.getCount()});
    });
    stream.on('error', error);
    stream.on('end', () => {
      setCategories((prev) => data);
    });
  }

  useEffect(() => listCategories(), []);

  return [categories];
}
