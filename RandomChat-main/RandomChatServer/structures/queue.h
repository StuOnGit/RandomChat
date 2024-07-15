#ifndef QUEUE_H
#define QUEUE_H

struct Node{
    void* data;
    struct Node* next;
};

typedef struct{
    struct Node* front;
    struct Node* back;
    unsigned int size;
}Queue;

void newQueue(Queue*);


void* head(Queue*);
void enqueue(Queue*, void*);
void* dequeue(Queue*);
void* extract(Queue* queue, void* data);
int getQueueSize(Queue* queue);

#endif
