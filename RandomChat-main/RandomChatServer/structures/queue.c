#include "queue.h"
#include <stdlib.h>
#include <stdio.h>
#include "user.h"

void newQueue(Queue* queue){
    queue->front = NULL;
    queue->back = NULL;
    queue->size = 0;
}

void* top(Queue* queue){
    if(queue->front != NULL){
        return queue->front->data;
    }
    return NULL;
}

int getQueueSize(Queue* queue){
    return queue->size;
}

void enqueue(Queue* queue, void* data){
    struct Node* node = malloc(sizeof(struct Node));
    node->data = data;
    node->next = NULL;

    if(queue->front == NULL){
        queue->front = node;
        queue->back = node;
    }else{
        queue->back->next = node;
        queue->back = node;
    }
    queue->size++;
}

void* dequeue(Queue* queue){
    if(queue->front != NULL){
        void* ret = queue->front->data;
        struct Node* delNode = queue->front;
        queue->front = queue->front->next;
        free(delNode);
        queue->size--;
        if(queue->size == 0){
            queue->back = NULL;
        }
        return ret;
    }
    return NULL;
}





void* extract(Queue* queue, void* data){
    if(queue->size != 0){
        if(data == queue->front->data){
            return dequeue(queue);
        }else{
            struct Node* tmp = queue->front;
            while(tmp->next != NULL){
                if(tmp->next->data == data){
                    struct Node* deleteNode = tmp->next;
                    tmp->next = deleteNode->next;
                    if(queue->back == deleteNode){
                        queue->back = tmp;
                    }
                    free(deleteNode);
                    queue->size--;
                    return data;
                }else{
                    tmp = tmp->next;
                }
            }   
        }
    }
    return NULL; //elemento non c'è o la coda è nulla.
}

