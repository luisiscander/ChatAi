Soluciona un problema basado en el identificador que se te pasa como argumento {id} o {id,id,id, etc} 0 {id..id}, creando una nueva rama que luego subiras como un pull request

usa el comando gh para comunicarte con Github
 ## pasos
 1. recupera la issue de Github
 2. crea nueva rama "feature-{id}"
 3.soluciona la issue y en caso de que sean varias de manera secuencial.
 4. /review
 5. si no hay errores  crea un pull request y mergea con la rama princial. para esto me preguntas
 6. marca la issue como "completada"
 7. si el paso 4 salio bien , elimina la rama "feature-{id}"