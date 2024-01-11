# System of informing the population about resources and life safety points
1. [Overview](#overview)
    * [Requirements](#requirements)
    * [Architecture](#architecture)
    * [Technology stack](#technology-stack)
    * [Database scheme](#database-scheme)
2. [Demonstration](#demonstration)
    * [Main page](#main-page)
    * [Point description](#point-description)
    * [Point creation](#point-creation)
    * [Admin Page](#admin-page)

## Overview

### Requirements
- Guest mode for viewing a map.
- There's an email-verified authorization system with password recovery.
- Authorized users can add points to the map. 
- Points have a rating system; users can like or dislike them. Points disliked over 20 times are hidden. 
- Administrators can manage users and their points on the map.

### Architecture
<img src="./images/structure.png" alt="layers"  style="width:70%;"/>

### Technology stack
<img src="./images/technologies.png" alt="TS"  style="width:70%;"/>

### Database scheme
<img src="./images/database.png" alt="db"  style="width:70%;"/>

## Demonstration

### Main page
<img src="./images/map.png" alt="map"  style="width:70%;"/>

### Point description
<img src="./images/point-desc.png" alt="point-desc"  style="width:70%;"/>

### Point creation
<img src="./images/point-creation.png" alt="point-creation"  style="width:70%;"/>

### Admin Page
Managing users and their points on the map</br>
<img src="./images/admin.png" alt="admin" style="width:70%;"/>