# PLUG-IN NAME
Chart Generator: This plugin contains plugin components to generate charts in Appian as Appian documents.

## NAME Smart Service
This smart service creates images of charts and saves them in Appian as Appian documents

### Data Tab
Chart Settings	Type	Yes	No
Create New Document	Boolean	Yes	No
New Document Name	Text	No	No
New Document Description	Text	No	No
Save In Folder	Folder	No	No
Existing Document	Document	No	No


| Input                    | Data Type | Required | Multiple | Description |
| ------------------------ |:---------:|:--------:|:--------:| ----------- |
| Chart Settings           | Text      | Yes      | No       | Chart Settings to configure in the smart service static input “Chart Settings”|
| Create New Document      | Boolean   | Yes      | No       | Set the input “Create New Document” to create a new Appian document for the chart  |
| New Document Name        | Text      | No       | No       | Use the input “New Document Name” to provide the name of the new document (if Create New Document = true) |
| New Document Description | Text      | No       | No       | Use the input “New Document Description” to provide the description of the new document (if Create New Document = true) |
| Save In Folder           | Folder    | No       | No       | Use the input “Save In Folder” to specify in which folder the new document is created (if Create New Document = true) |
| SExisting Document       | Document  | No       | No       | Set the input “Existing Document” to an existing document to specify that the new chart is created as a new version of this existing document instead of as a new document altogether |

| Output              | Data Type | Multiple | Description |
| --------------------|:---------:|:--------:| ----------- |
| Generated Chart     | Document  | No       | Returns the generated Document reference |

---
