#!/usr/bin/env bash
jar_file=chord-1.0-SNAPSHOT-full.jar


######
# usage:
#   - laptop with anchor:
#         ./start-chord-net.sh <NUM_NODES> <MY_IP> anchor
#
#   - laptop without anchor:
#         ./start-chord-net.sh <NUM_NODES> <MY_IP> <ANCHOR_IP>
#
######

if (( $# != 3))
then
    echo "Wrong num of parameters. Usage:"
    echo " - laptop with anchor:"
    echo "     ./start-chord-net.sh <NUM_NODES> <MY_IP> anchor"
    echo " - laptop without anchor:"
    echo "     ./start-chord-net.sh <NUM_NODES> <MY_IP> <ANCHOR_IP>"
    exit 1
fi

if [[ -d ./target ]] && [[ -f "./target/$jar_file" ]]
then

	# Move in target chord directory
	cd ./target

    number_node=$1
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

    my_ip=$2
    if [[ "$3" == "anchor" ]]
    then  # start the anchor
        anchor_ip=${my_ip}

        # Anchor node
        tmux select-pane -t 0
        tmux send-keys "java -jar $jar_file --local-host $my_ip --local-port 8888" C-m
        sleep 4

    else  # anchor node on the other pc
        anchor_ip=$3

        # Node panel 0
        tmux select-pane -t 0
        tmux send-keys "java -jar $jar_file --join --anchor-host $anchor_ip --anchor-port 8888 --local-host $my_ip --local-port 8888" C-m
        sleep 4

    fi

    i=1
    while [[ ${i} -lt ${number_node} ]]
    do
        tmux select-pane -t "$i"
        port_i=$((i+9000))
        tmux send-keys "java -jar $jar_file --join --anchor-host $anchor_ip --anchor-port 8888 --local-host $my_ip --local-port $port_i" C-m
        sleep 4
        i=$((i+1))
    done

	tmux attach-session -t chord

else
	echo "No jar file found. Check path or run 'mvn clean package'"
fi
