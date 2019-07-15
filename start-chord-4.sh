#!/usr/bin/env bash
jar_file=chord-1.0-SNAPSHOT-full.jar

if [[ -d ./target ]] && [[ -f "./target/$jar_file" ]]
then

	# Move in target chord directory
	cd ./target

	# Start tmux server
	tmux start-server
	tmux new -d -s chord
	tmux new-window -t chord:1 -n "Chord nodes"

	# Split window vertically
	tmux split-window -v

	# Split top pane
	tmux select-pane -t 0
	tmux split-window -h

	# Anchor node
	tmux select-pane -t 0
	tmux send-keys "java -jar $jar_file --local-port 8888" C-m

	# Wait for anchor start
	sleep 4

	# Node 1
	tmux select-pane -t 1
	tmux send-keys "java -jar $jar_file --join --anchor-port 8888 --local-port 8893" C-m

	# Split bottom pane horizontally
	tmux select-pane -t 2
	tmux split-window -h

	# Node 2
	tmux select-pane -t 2
	tmux send-keys "java -jar $jar_file --join --anchor-port 8888 --local-port 9000" C-m

	# Node 3
	tmux select-pane -t 3
	tmux send-keys "java -jar $jar_file --join --anchor-port 8888 --local-port 7706" C-m

	# Enable mouse in windows (for move between panes by click)
	tmux setw mouse on

	# Attach to session
	tmux attach-session -t chord

else
	echo "No jar file found. Check path or run 'mvn clean package'"
fi
