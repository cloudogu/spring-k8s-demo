# Installation

```bash
kubectl apply -f deployments/rbac.yml
helm init --service-account tiller
curl -L https://git.io/getLatestIstio | ISTIO_VERSION=1.1.6 sh
cd istio-1.1.6
helm install install/kubernetes/helm/istio-init --name istio-init --namespace istio-system

# verify crd installation, should be 53
kubectl get crds | grep 'istio.io\|certmanager.k8s.io' | wc -l

# install with mutual tls
helm install install/kubernetes/helm/istio --name istio --namespace istio-system \
    --values install/kubernetes/helm/istio/values-istio-demo-auth.yaml
```

# Enable sidecar injection for default namespace

```bash
kubectl label namespace default istio-injection=enabled
kubectl get namespace -L istio-injection
```

# Deploy applications

```bash
helm install --name main --set jaeger.host=jaeger-agent.istio-system --set ingress.enabled=false --set istio.auth=true --values deployment/main.yml helm/spring-k8s-demo

helm install --name one --set jaeger.host=jaeger-agent.istio-system --set istio.auth=true helm/spring-k8s-demo
helm install --name two --set jaeger.host=jaeger-agent.istio-system --set istio.auth=true helm/spring-k8s-demo
helm install --name three --set jaeger.host=jaeger-agent.istio-system --set istio.auth=true helm/spring-k8s-demo
```

## Configure Routing

```bash
kubectl apply -f istio/routing.yml
```

# UI's

```bash
# jaeger
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686

# kiali (ctx path /kiali)
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=kiali -o jsonpath='{.items[0].metadata.name}') 20001

# grafana
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000

# prometheus
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 9090
```

### Notes

* Timeouts: with timeout.yml and /remote/counter-slow/one-spring-k8s-demo
* Retries: with retry.yml and /remote/counter/two-spring-k8s-demo
* Routing: with 50-50-split.yml  and /remote/hostname/three-spring-k8s-demo
