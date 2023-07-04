# PlayHex

---

## Languages

+ [English (United States)](./README.md)
+ [简体中文（中国大陆）](./docs/READMEs/README.zh_CN.md)

---

## Description

PlayHex, a board game, as the homework of the course 'JAVA and Object-Oriented Programming' in Tsinghua University in 2023.

## Platform

Java 17

## Copyright

[MIT LICENSE](./LICENSE)

## Overview

![overview](./assets/overview.png)

## Game Rules

The red and blue sides take turns moving, with red going first. During each turn, players can choose one of their own chess pieces and move it in any direction by 1 to 2 squares (the destination square must be an empty space on the board). If a player chooses to move 1 square, the original chess piece remains in place after landing on the target square, thus increasing the total number of pieces by one. If a player chooses to move 2 squares, the original chess piece disappears after landing on the target square, keeping the total number of pieces unchanged.

When a player makes a move and there are opponent's chess pieces in the adjacent squares (1 square away), all those pieces are captured, meaning they change color. If a player's turn comes up and they are unable to make a move, the remaining empty spaces are filled with the other player's chess pieces, and the game ends. The side with more chess pieces on the board wins.

## Operation

+ "WASD" or arrow keys to move cursor
+ "Enter" or "Space" to select
+ "Q", "Esc" or "Numpad 0" to cancel selection

**Thank you for playing!**
