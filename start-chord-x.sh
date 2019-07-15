#!/usr/bin/env bash
jar_file=chord-1.0-SNAPSHOT-full.jar


if (( $# != 1 ))
then
    number_node=9
else
    number_node=$1
fi


if [[ -d ./target ]] && [[ -f "./target/$jar_file" ]]
then

	# Move in target chord directory
	cd ./target

	echo "Please, be patient, it is going to take approx $(($number_node*4))s..."

	# Start tmux server
	tmux start-server
	tmux new -d -s chord
	tmux new-window -t chord:1 -n "Chord nodes"

	# Split windows
	i=1
    while [[ ${i} -lt ${number_node} ]]
    do
	    tmux split-window -v
	    tmux select-layout tiled
	    i=$((i+1))
	done

    # Anchor node
    tmux select-pane -t 0
    tmux send-keys "java -jar $jar_file --local-port 8888" C-m
    # Wait for anchor start
    sleep 4

    i=1
    while [[ ${i} -lt ${number_node} ]]
    do
        tmux select-pane -t "$i"
        port_i=$((i+9000))
        tmux send-keys "java -jar $jar_file --join --anchor-port 8888 --local-port $port_i" C-m
        sleep 4
        i=$((i+1))
    done

	tmux attach-session -t chord

else
	echo "No jar file found. Check path or run 'mvn clean package'"
fi
