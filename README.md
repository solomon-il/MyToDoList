# MyToDoList  -  Android app: To-Do list. 
> dialog, custom dialog, sqlite db, custom spinner, adapters

  - Click on the "Add" button to open new dialog.
  - Enter: Title, Description and Priority (Urgent, Medium and Low) and hit "Save".
    - it save the data into sqlite db and into the list.
  - Main Activity now contains a List view with the task you entered.
    - items has a color for Priority(red: urgent, orange: medium, yellow: low), Title and CheckBox of "complete task"
    - items are sorted by: completion (not complete first), priority (urgent first) and last by date entered.
  - Press on item will show a dialog with more details of the task.
  - Long press on item will show a dialog for delete the task (yes or no).

