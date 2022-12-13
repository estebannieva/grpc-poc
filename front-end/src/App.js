import './App.css';
import {useEffect, useState} from 'react';
import {useTasks} from './hooks/useTasks';
import {useCategories} from './hooks/useCategories';
import {useHtml} from './hooks/useHtml';

function App() {
  const [toggle, setToggle] = useState(false);
  const [category, setCategory] = useState(null);
  const [categories] = useCategories();
  const [tasks, count, percent, createTask, deleteTask, downloadTasks] = useTasks(category?.id);

  const handleHidden = (ev) => {
    let elem = ev.target.parentElement;
    while (elem !== null) {
      if (elem.getAttribute('role') === 'menubar') break;
      elem = elem.parentElement;
    }
    if (elem === null) {
      setToggle(false);
    }
  }

  const handleSelect = (ev) => {
    setCategory(JSON.parse(ev.target.dataset.category));
    setToggle(false);
  }

  const handleToggle = () => {
    setToggle(!toggle);
  }

  const handleDownload = async () => {
    await downloadTasks();
  }

  const handleCreate = (ev) => {
    if (ev.key === 'Enter') {
      createTask(ev.target.value);
      ev.target.value = '';
    }
  }

  const handleDelete = (ev) => {
    try {
      deleteTask(ev.currentTarget.dataset.id);
      let elem = ev.currentTarget;
      while (elem !== null) {
        if (elem.tagName === 'LI') break;
        elem = elem.parentElement;
      }
      if (elem !== null) {
        elem.remove()
      }
    } catch (e) {
      console.log('>> ERROR')
      console.log(e)
    }
  }

  useHtml(handleHidden, 'bg-white', 'dark:bg-[#171717]');

  useEffect(() => {
    if (categories.length !== 0) setCategory({id: categories.at(0).id, name: categories.at(0).name});
  }, [categories]);

  return (
    <div className="container mx-auto px-4">
      <div className="flex flex-col gap-y-4 max-w-3xl mx-auto mt-36">
        <div className="flex flex-col gap-y-4 ml-2">
          <span className="text-7xl">&#x1F4E6;</span>
          <div className="flex flex-row items-center gap-x-2">
            <div className="relative" role="menubar">
              <div>
                <button onClick={handleToggle} type="button" className="inline-flex justify-center px-4 py-2 text-sm text-gray-500 bg-gray-50 dark:bg-[#1b1b1b] hover:bg-gray-100 dark:hover:bg-[#262626] rounded" aria-expanded="true" aria-haspopup="true">
                  {category?.name}
                  <svg className="-mr-1 ml-2 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
                  </svg>
                </button>
              </div>
              {
                toggle && <div className="absolute right-0 z-10 mt-2 w-56 origin-top-right bg-white dark:bg-[#1b1b1b] ring-1 dark:ring-0 ring-gray-50 focus:outline-none shadow-md rounded" role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabIndex="-1">
                  <div className="py-1" role="none">
                    {
                      categories.map((category, index) =>
                        <a onClick={handleSelect} key={index} href={`#${category.name.toLowerCase()}`} data-category={JSON.stringify(category)} className="text-gray-900 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-[#262626] block px-4 py-2 text-sm" role="menuitem" tabIndex="-1">{category.name}</a>
                      )
                    }
                  </div>
                </div>
              }
            </div>
            <button onClick={handleDownload} className="inline-flex justify-center px-4 py-2 text-sm text-gray-500 bg-gray-50 dark:bg-[#1b1b1b] hover:bg-gray-100 dark:hover:bg-[#262626] rounded">
              Download
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="-mr-1 ml-2 h-5 w-5 text-gray-400">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m0 0l6.75-6.75M12 19.5l-6.75-6.75"/>
              </svg>
            </button>
            {
              percent !== null && <span className="text-sm text-gray-500">{percent}% { percent === 100 && 'save completed' }</span>
            }
          </div>
          <h1 className="text-3xl tracking-tight font-bold text-gray-900 dark:text-gray-200">To-Do List</h1>
          <div className="text-gray-900 dark:text-gray-200">Showing {count} entries</div>
          <input onKeyDown={handleCreate} type="text" autoFocus placeholder="Add a new task..." className="w-full appearance-none bg-gray-50 dark:bg-[#1b1b1b] rounded py-4 pl-4 pr-12 text-base text-gray-900 dark:text-gray-200 placeholder:text-gray-500 focus:outline-none sm:text-sm sm:leading-6" />
        </div>
        <ul className="flex flex-col gap-y-2">
          {
            tasks.map((task, index) =>
              <li key={index} className="list-decimal text-gray-900 dark:text-gray-200">
                <div className="flex justify-between gap-x-2 text-gray-900 dark:text-gray-200 group hover:bg-gray-100 dark:hover:bg-[#262626] rounded py-1 px-2">
                  <span className="grow text-base lowercase truncate">{task.name}</span>
                  <button onClick={handleDelete} data-id={task.id} className="flex-none hidden group-hover:block">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-gray-500 dark:text-gray-200" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                  </button>
                </div>
              </li>
            )
          }
        </ul>
      </div>
    </div>
  );
}

export default App;
