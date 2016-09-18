# spaceBallServer
### Message Format
All messages are exchanged in plain text, arguments delimited by comma

### Server Message Format
```
msg_type,arg1,arg2,arg3....

msg_type:
1=init
  no args
  
2=birth
  arg1=id
  arg2=x
  arg3=y
  arg4=z
  arg5=size
  
3=list_other_players
  arg1=[id,x,y,z,color,material,size,name]
  arg2=...
  
4=list_food
  arg1=[id,x,y,z,color,material,size]
  arg2=...
  
5=leaders
  arg1=[id,name,score]
  arg2=...
  
99=error
  arg1=error message
```

### Client Message Format
```
msg_type,arg1,arg2,arg3....

msg_type:
1=birth
  arg1=color
  arg2=material
  arg3=name
  
2=movement
  arg1=x
  arg2=y
  arg3=z
  
3=eat_food
  arg1=food_id
  arg2=new_size
  arg3=new_score
  
4=eat_player
  arg1=player_id
  arg2=new_size
  arg3=new_score
  
99=death
  no args
  
```

## Procedure
```
Client: connect
Server: init

while true do

  Client: birth
  Server: birth
  ...
  Server: list_other_players
  Client: movement
  Client: growth
  ...
  Client: death

done
```
