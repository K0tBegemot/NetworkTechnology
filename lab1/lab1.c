#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
//#include <arpa/inet.h>
//#include <netinet/in.h>
#include <netdb.h>
#include <time.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#define GROUP_INDEX 1
#define PORT_INDEX 2
#define MESSAGE_BUF 100
#define TTL_TIME 8
#define LOOP_BACK_MESSAGES 1
#define TIME_TO_SLEEP 1
#define NEXT_ITER_TIME 3
#define SET_OPTION_NO 0
#define SET_OPTION_YES 1
#define JOIN_GROUP_NO -1
#define JOIN_GROUP_YES 0

#define REUSE_ADDR_VALUE 1

int setReuseAddrOption(int sockFD)
{
    const int optVal = REUSE_ADDR_VALUE;
    int retCode = setsockopt(sockFD, SOL_SOCKET, SO_REUSEADDR, &optVal, sizeof(optVal));
    if (retCode != 0)
    {
        perror(NULL);
        return SET_OPTION_NO;
    }
    return SET_OPTION_YES;
}

int getAddr(const char *node, const char *service, int family, int socktype, struct sockaddr_storage *addr)
{
    int retVal = -1;
    struct addrinfo hints;
    struct addrinfo *ret;
    struct addrinfo *retCopy;
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = family;
    hints.ai_socktype = socktype;
    int retCode = getaddrinfo(node, service, &hints, &ret);
    if (retCode != 0)
    {
        fprintf(stderr, "Error in function getaddrinfo in getAddr. [%s]\n", gai_strerror(retCode));
        return retVal;
    }
    retCopy = ret;
    int sockFD = -1;
    while (ret != NULL)
    {
        sockFD = socket(ret->ai_family, ret->ai_socktype, ret->ai_protocol);
        if (sockFD >= 0)
        {
            retCode = setReuseAddrOption(sockFD);
            if (bind(sockFD, ret->ai_addr, ret->ai_addrlen) == 0)
            {
                close(sockFD);
                memcpy(addr, ret->ai_addr, ret->ai_addrlen);
                retVal = ret->ai_addrlen;
                break;
            }
            else
            {
                perror(NULL);
            }
        }
        ret = ret->ai_next;
    }
    freeaddrinfo(retCopy);
    return retVal;
}

int joinGroup(int sockFD, int loopBack, int mcastTTL, struct sockaddr_storage *addr)
{
    int retVal = JOIN_GROUP_NO;
    int retCode;
    switch (addr->ss_family)
    {
    case AF_INET:
    {
        // retCode = setReuseAddrOption(sockFD);
        // if (retCode == SET_OPTION_NO)
        // {
        //     return JOIN_GROUP_NO;
        // }

        struct ip_mreq req;
        req.imr_multiaddr.s_addr = (((struct sockaddr_in *)addr)->sin_addr.s_addr);
        req.imr_interface.s_addr = INADDR_ANY;

        retCode = setsockopt(sockFD, IPPROTO_IP, IP_MULTICAST_LOOP, (const void *)&loopBack, sizeof(loopBack));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        retCode = setsockopt(sockFD, IPPROTO_IP, IP_MULTICAST_TTL, (const void *)&mcastTTL, sizeof(mcastTTL));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        retCode = setsockopt(sockFD, IPPROTO_IP, IP_ADD_MEMBERSHIP, (const void *)&req, sizeof(req));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        break;
    }
    case AF_INET6:
    {
        // retCode = setReuseAddrOption(sockFD);
        // if (retCode == SET_OPTION_NO)
        // {
        //     return JOIN_GROUP_NO;
        // }

        struct ipv6_mreq req;
        memcpy(&req.ipv6mr_multiaddr, &((struct sockaddr_in6 *)addr)->sin6_addr, sizeof(struct in6_addr));
        req.ipv6mr_interface = 0;

        retCode = setsockopt(sockFD, IPPROTO_IPV6, IPV6_MULTICAST_LOOP, (const void *)&loopBack, sizeof(loopBack));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        retCode = setsockopt(sockFD, IPPROTO_IPV6, IPV6_MULTICAST_HOPS, (const void *)&mcastTTL, sizeof(mcastTTL));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        retCode = setsockopt(sockFD, IPPROTO_IPV6, IPV6_ADD_MEMBERSHIP, (const void *)&req, sizeof(req));
        if (retCode != 0)
        {
            return JOIN_GROUP_NO;
        }

        break;
    }
    }
    return JOIN_GROUP_YES;
}

int isMulticast(struct sockaddr_storage *addr)
{
    int retVal = -1;
    switch (addr->ss_family)
    {
    case (AF_INET):
    {
        struct sockaddr_in *addr4 = (struct sockaddr_in *)addr;
        retVal = IN_MULTICAST(ntohl(addr4->sin_addr.s_addr));
    }
    case (AF_INET6):
    {
        struct sockaddr_in6 *addr6 = (struct sockaddr_in6 *)addr;
        retVal = IN6_IS_ADDR_MULTICAST(&addr6->sin6_addr);
    }
    }
    return retVal;
}

void initialiseDefStructVal(struct sockaddr_storage *addr)
{
    memset(addr, 0, sizeof(*addr));
}

void sendMessage(int sockfd, struct sockaddr_storage *addr, socklen_t addrlen)
{
    char timeStr[99];
    memset(timeStr, 0, sizeof(timeStr));
    time_t now;
    time(&now);
    int length = sprintf(timeStr, "%s", ctime(&now));
    int numberOfSend = sendto(sockfd, timeStr, length * sizeof(char), 0, (struct sockaddr *)(addr), addrlen);
}

int main(int argc, char **argv)
{
    // if (argc == 3)
    {
        struct sockaddr_storage addr;
        char *multicastAddr = "224.0.1.1"; //"FF01::1111";//argv[GROUP_INDEX];
        char *portAddr = "10000";           // argv[PORT_INDEX];
        initialiseDefStructVal(&addr);
        int addrlen;
        if ((addrlen = getAddr(multicastAddr, portAddr, AF_UNSPEC, SOCK_DGRAM, &addr)) < 0)
        {
            fprintf(stderr, "Error in function getAddr: couldn't find multicast.\nAddress [%s] Port [%s]", multicastAddr, portAddr);
            return -1;
        }
        if (isMulticast(&addr) < 0)
        {
            fprintf(stderr, "This address is not multicast one. Address [%s]\n", multicastAddr);
            return -1;
        }
        int sockfd = socket(addr.ss_family, SOCK_DGRAM, 0);
        int retCode = setReuseAddrOption(sockfd);
        if (bind(sockfd, (struct sockaddr *)(&addr), sizeof(addr)) < 0)
        {
            perror("Error in function bind: ");
            close(sockfd);
            return -1;
        }
        if (joinGroup(sockfd, LOOP_BACK_MESSAGES, TTL_TIME, &addr) < 0)
        {
            close(sockfd);
            return -1;
        }
        //
        struct sockaddr_storage clientAddr;
        socklen_t clientAddrlen;
        initialiseDefStructVal(&clientAddr);
        char multicastMessageBuf[MESSAGE_BUF];
        char multicastClientHost[NI_MAXHOST];
        char multicastClientService[NI_MAXSERV];
        sendMessage(sockfd, &addr, addrlen);
        int iteration = 1;
        while (iteration)
        {
            int numberOfRecv = recvfrom(sockfd, multicastMessageBuf, sizeof(multicastMessageBuf), 0, (struct sockaddr *)(&clientAddr), &clientAddrlen);
            if (numberOfRecv == -1)
            {
                perror("There is error on blocking call recvfrom. Error message:\n");
            }
            memset(multicastClientHost, 0, sizeof(multicastClientHost));
            memset(multicastClientService, 0, sizeof(multicastClientService));
            getnameinfo((struct sockaddr *)(&clientAddr), clientAddrlen, multicastClientHost, sizeof(multicastClientHost), multicastClientService, sizeof(multicastClientService), 0);
            fprintf(stdout, "\n\nIteration number %d. List of all multicast devices:\n\n", iteration);
            fprintf(stdout, "Message: %s . Received request from host [%s]\n", multicastMessageBuf, multicastClientHost);
            //
            while (1)
            {
                //

                int counter = 0;

                do
                {

                    int numberOfRecv = recvfrom(sockfd, multicastMessageBuf, sizeof(multicastMessageBuf), MSG_DONTWAIT, (struct sockaddr *)(&clientAddr), &clientAddrlen);
                    if (numberOfRecv == -1 && errno == EAGAIN)
                    {
                        sleep(TIME_TO_SLEEP);
                        counter += TIME_TO_SLEEP;
                    }
                    else
                    {
                        memset(multicastClientHost, 0, sizeof(multicastClientHost));
                        memset(multicastClientService, 0, sizeof(multicastClientService));
                        getnameinfo((struct sockaddr *)(&clientAddr), clientAddrlen, multicastClientHost, sizeof(multicastClientHost), multicastClientService, sizeof(multicastClientService), NI_NUMERICHOST);
                        fprintf(stdout, "Message: %s . Received request from host [%s]\n", multicastMessageBuf, multicastClientHost);
                    }

                } while (counter < NEXT_ITER_TIME);
                if(counter >= NEXT_ITER_TIME)
                {
                    break;
                }
            }
            sendMessage(sockfd, &addr, addrlen);
            iteration += 1;
        }
        //

        return 0;
    }
    return 0;
}